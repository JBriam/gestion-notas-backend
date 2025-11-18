package edu.college.gestion_notas_backend.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.college.gestion_notas_backend.dto.request.ActualizarNotaDTO;
import edu.college.gestion_notas_backend.dto.request.CrearNotaDTO;
import edu.college.gestion_notas_backend.dto.response.NotaResponseDTO;
import edu.college.gestion_notas_backend.model.Curso;
import edu.college.gestion_notas_backend.model.Estudiante;
import edu.college.gestion_notas_backend.model.Nota;
import edu.college.gestion_notas_backend.service.CursoService;
import edu.college.gestion_notas_backend.service.EstudianteService;
import edu.college.gestion_notas_backend.service.NotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Notas", description = "API para la gestión de calificaciones académicas")
@RestController
@RequestMapping("/notas")
@RequiredArgsConstructor
public class NotaController {
    
    private final NotaService notaService;
    private final EstudianteService estudianteService;
    private final CursoService cursoService;
    
    @Operation(
        summary = "Crear una nueva nota",
        description = "Registra una calificación para un estudiante en un curso específico. " +
                     "Incluye el tipo de evaluación y observaciones opcionales."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Nota creada exitosamente",
            content = @Content(schema = @Schema(implementation = NotaResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o estudiante/curso no encontrado",
            content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto al crear la nota",
            content = @Content)
    })
    @PostMapping
    public ResponseEntity<NotaResponseDTO> crearNota(@Valid @RequestBody CrearNotaDTO crearNotaDTO) {
        try {
            // Obtener estudiante y curso
            Optional<Estudiante> estudianteOpt = estudianteService.obtenerEstudiantePorId(crearNotaDTO.getIdEstudiante());
            Optional<Curso> cursoOpt = cursoService.obtenerCursoPorId(crearNotaDTO.getIdCurso());
            
            if (estudianteOpt.isEmpty() || cursoOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            Nota nota = Nota.builder()
                .estudiante(estudianteOpt.get())
                .curso(cursoOpt.get())
                .nota(crearNotaDTO.getNota())
                .tipoEvaluacion(Nota.TipoEvaluacion.valueOf(crearNotaDTO.getTipoEvaluacion()))
                .observaciones(crearNotaDTO.getObservaciones())
                .build();
            
            Nota notaCreada = notaService.crearNota(nota);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(notaCreada));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    
    // Obtener todas las notas
    @Operation(
        summary = "Obtener todas las notas",
        description = "Recupera la lista completa de calificaciones registradas."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de notas obtenida exitosamente"),
        @ApiResponse(responseCode = "404", description = "Notas no encontradas")
    })
    @GetMapping
    public ResponseEntity<List<NotaResponseDTO>> obtenerTodasLasNotas() {
        List<Nota> notas = notaService.obtenerTodasLasNotas();
        List<NotaResponseDTO> notasDTO = notas.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(notasDTO);
    }
    
    // Obtener nota por ID
    @Operation(
        summary = "Obtener nota por ID",
        description = "Busca una nota específica por su identificador."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nota encontrada"),
        @ApiResponse(responseCode = "404", description = "Nota no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<NotaResponseDTO> obtenerNotaPorId(@PathVariable Integer id) {
        Optional<Nota> nota = notaService.obtenerNotaPorId(id);
        return nota.map(n -> ResponseEntity.ok(convertirADTO(n)))
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(
        summary = "Obtener notas por estudiante",
        description = "Recupera todas las calificaciones de un estudiante específico."
    )
    @ApiResponse(responseCode = "200", description = "Lista de notas del estudiante")
    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<List<NotaResponseDTO>> obtenerNotasPorEstudiante(
            @Parameter(description = "ID del estudiante", required = true) @PathVariable Integer idEstudiante) {
        List<Nota> notas = notaService.obtenerNotasPorIdEstudiante(idEstudiante);
        List<NotaResponseDTO> notasDTO = notas.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(notasDTO);
    }
    
    // Obtener notas por curso
    @Operation(
        summary = "Obtener notas por curso",
        description = "Recupera todas las calificaciones de un curso específico."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de notas del curso"),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    @GetMapping("/curso/{idCurso}")
    public ResponseEntity<List<NotaResponseDTO>> obtenerNotasPorCurso(@PathVariable Integer idCurso) {
        List<Nota> notas = notaService.obtenerNotasPorIdCurso(idCurso);
        List<NotaResponseDTO> notasDTO = notas.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(notasDTO);
    }
    
    // Obtener notas por tipo de evaluación
    @Operation(
        summary = "Obtener notas por tipo de evaluación",
        description = "Recupera todas las calificaciones de un tipo de evaluación específico."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de notas del tipo de evaluación"),
        @ApiResponse(responseCode = "404", description = "Tipo de evaluación no encontrado")
    })
    @GetMapping("/tipo/{tipoEvaluacion}")
    public ResponseEntity<List<NotaResponseDTO>> obtenerNotasPorTipo(@PathVariable String tipoEvaluacion) {
        try {
            Nota.TipoEvaluacion tipo = Nota.TipoEvaluacion.valueOf(tipoEvaluacion.toUpperCase());
            List<Nota> notas = notaService.obtenerNotasPorTipoEvaluacion(tipo);
            List<NotaResponseDTO> notasDTO = notas.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
            return ResponseEntity.ok(notasDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(
        summary = "Calcular promedio por estudiante",
        description = "Calcula el promedio general de todas las notas de un estudiante."
    )
    @ApiResponse(responseCode = "200", description = "Promedio calculado")
    @GetMapping("/promedio/estudiante/{idEstudiante}")
    public ResponseEntity<Double> calcularPromedioPorEstudiante(
            @Parameter(description = "ID del estudiante", required = true) @PathVariable Integer idEstudiante) {
        Double promedio = notaService.calcularPromedioPorEstudiante(idEstudiante);
        return ResponseEntity.ok(promedio);
    }
    
    // Calcular promedio por curso
    @Operation(
        summary = "Calcular promedio por curso",
        description = "Calcula el promedio general de todas las notas de un curso."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Promedio calculado"),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    @GetMapping("/promedio/curso/{idCurso}")
    public ResponseEntity<Double> calcularPromedioPorCurso(@PathVariable Integer idCurso) {
        Double promedio = notaService.calcularPromedioPorCurso(idCurso);
        return ResponseEntity.ok(promedio);
    }
    
    // Calcular promedio por estudiante y curso
    @Operation(
        summary = "Calcular promedio por estudiante y curso",
        description = "Calcula el promedio general de todas las notas de un estudiante en un curso específico."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Promedio calculado"),
        @ApiResponse(responseCode = "404", description = "Estudiante o curso no encontrado")
    })
    @GetMapping("/promedio/estudiante/{idEstudiante}/curso/{idCurso}")
    public ResponseEntity<Double> calcularPromedioPorEstudianteYCurso(
            @PathVariable Integer idEstudiante, 
            @PathVariable Integer idCurso) {
        Double promedio = notaService.calcularPromedioPorEstudianteYCurso(idEstudiante, idCurso);
        return ResponseEntity.ok(promedio);
    }
    
    @Operation(
        summary = "Obtener estado académico",
        description = "Determina el estado académico de un estudiante en un curso (APROBADO/DESAPROBADO)."
    )
    @ApiResponse(responseCode = "200", description = "Estado académico obtenido")
    @GetMapping("/estado/estudiante/{idEstudiante}/curso/{idCurso}")
    public ResponseEntity<String> obtenerEstadoAcademico(
            @Parameter(description = "ID del estudiante", required = true) @PathVariable Integer idEstudiante, 
            @Parameter(description = "ID del curso", required = true) @PathVariable Integer idCurso) {
        String estado = notaService.obtenerEstadoAcademico(idEstudiante, idCurso);
        return ResponseEntity.ok(estado);
    }
    
    // Obtener notas aprobatorias
    @Operation(
        summary = "Obtener notas aprobatorias",
        description = "Obtiene todas las notas que son consideradas aprobatorias."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notas aprobatorias obtenidas exitosamente"),
        @ApiResponse(responseCode = "404", description = "No se encontraron notas aprobatorias")
    })
    @GetMapping("/aprobatorias")
    public ResponseEntity<List<NotaResponseDTO>> obtenerNotasAprobatorias() {
        List<Nota> notas = notaService.obtenerNotasAprobatorias();
        List<NotaResponseDTO> notasDTO = notas.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(notasDTO);
    }
    
    // Obtener mejores notas por curso
    @Operation(
        summary = "Obtener mejores notas por curso",
        description = "Obtiene las mejores notas de un curso específico."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mejores notas obtenidas exitosamente"),
        @ApiResponse(responseCode = "404", description = "No se encontraron notas para el curso especificado")
    })
    @GetMapping("/mejores/curso/{idCurso}")
    public ResponseEntity<List<NotaResponseDTO>> obtenerMejoresNotasPorCurso(
            @PathVariable Integer idCurso, 
            @RequestParam(defaultValue = "10") int limite) {
        List<Nota> notas = notaService.obtenerMejoresNotasPorCurso(idCurso, limite);
        List<NotaResponseDTO> notasDTO = notas.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(notasDTO);
    }
    
    @Operation(
        summary = "Actualizar nota",
        description = "Modifica una calificación existente."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nota actualizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Nota no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<NotaResponseDTO> actualizarNota(
            @Parameter(description = "ID de la nota", required = true) @PathVariable Integer id, 
            @Valid @RequestBody ActualizarNotaDTO notaDTO) {
        try {
            Nota notaActualizada = Nota.builder()
                .nota(notaDTO.getNota())
                .tipoEvaluacion(Nota.TipoEvaluacion.valueOf(notaDTO.getTipoEvaluacion()))
                .observaciones(notaDTO.getObservaciones())
                .build();
            
            Nota nota = notaService.actualizarNota(id, notaActualizada);
            return ResponseEntity.ok(convertirADTO(nota));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Eliminar nota
    @Operation(
        summary = "Eliminar nota",
        description = "Elimina una calificación existente."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nota eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Nota no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNota(@PathVariable Integer id) {
        try {
            notaService.eliminarNota(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Obtener estadísticas por tipo de evaluación
    @Operation(
        summary = "Obtener estadísticas por tipo de evaluación",
        description = "Obtiene estadísticas agrupadas por tipo de evaluación."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente"),
        @ApiResponse(responseCode = "404", description = "No se encontraron estadísticas")
    })
    @GetMapping("/estadisticas/tipo")
    public ResponseEntity<List<Object[]>> obtenerEstadisticasPorTipo() {
        List<Object[]> estadisticas = notaService.obtenerEstadisticasPorTipoEvaluacion();
        return ResponseEntity.ok(estadisticas);
    }
    
    // Método de conversión
    private NotaResponseDTO convertirADTO(Nota nota) {
        String estadoAcademico = notaService.obtenerEstadoAcademico(
            nota.getEstudiante().getIdEstudiante(), 
            nota.getCurso().getIdCurso());
        
        return NotaResponseDTO.builder()
            .idNota(nota.getIdNota())
            .nota(nota.getNota())
            .tipoEvaluacion(nota.getTipoEvaluacion().toString())
            .fechaRegistro(nota.getFechaRegistro())
            .observaciones(nota.getObservaciones())
            .idEstudiante(nota.getEstudiante().getIdEstudiante())
            .nombreEstudiante(nota.getEstudiante().getNombres())
            .apellidosEstudiante(nota.getEstudiante().getApellidos())
            .codigoEstudiante(nota.getEstudiante().getCodigoEstudiante())
            .idCurso(nota.getCurso().getIdCurso())
            .nombreCurso(nota.getCurso().getNombre())
            .codigoCurso(nota.getCurso().getCodigoCurso())
            .estadoAcademico(estadoAcademico)
            .build();
    }
}