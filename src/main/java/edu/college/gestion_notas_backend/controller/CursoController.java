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

import edu.college.gestion_notas_backend.dto.request.CrearCursoDTO;
import edu.college.gestion_notas_backend.dto.response.CursoResponseDTO;
import edu.college.gestion_notas_backend.model.Curso;
import edu.college.gestion_notas_backend.model.Docente;
import edu.college.gestion_notas_backend.service.CursoService;
import edu.college.gestion_notas_backend.service.DocenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Cursos", description = "API para la gestión de cursos académicos")
@RestController
@RequestMapping("/cursos")
@RequiredArgsConstructor
public class CursoController {
    
    private final CursoService cursoService;
    private final DocenteService docenteService;
    
    @Operation(
        summary = "Crear un nuevo curso",
        description = "Crea un nuevo curso académico con la información proporcionada. " +
                     "El código del curso se genera automáticamente si no se proporciona. " +
                     "Opcionalmente puede asociarse con un docente."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Curso creado exitosamente",
            content = @Content(schema = @Schema(implementation = CursoResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflicto - El código del curso ya existe",
            content = @Content),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud",
            content = @Content)
    })
    @PostMapping
    public ResponseEntity<CursoResponseDTO> crearCurso(@Valid @RequestBody CrearCursoDTO crearCursoDTO) {
        try {
            String codigo = crearCursoDTO.getCodigoCurso() != null ? crearCursoDTO.getCodigoCurso() : cursoService.generarCodigoCurso();
            Optional<Curso> cursoExistente = cursoService.obtenerCursoPorCodigo(codigo);
            if (cursoExistente.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            Curso curso = Curso.builder()
                .nombre(crearCursoDTO.getNombre())
                .codigoCurso(codigo)
                .descripcion(crearCursoDTO.getDescripcion())
                .creditos(crearCursoDTO.getCreditos())
                .build();
            
            // Si se proporciona ID de docente, asociarlo
            if (crearCursoDTO.getIdDocente() != null) {
                Optional<Docente> docente = docenteService.obtenerDocentePorId(crearCursoDTO.getIdDocente());
                docente.ifPresent(curso::setDocente);
            }
            
            Curso cursoCreado = cursoService.crearCurso(curso);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(cursoCreado));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    
    @Operation(
        summary = "Obtener todos los cursos",
        description = "Recupera la lista completa de cursos registrados en el sistema, " +
                     "incluyendo tanto activos como inactivos."
    )
    @ApiResponse(responseCode = "200", description = "Lista de cursos obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<CursoResponseDTO>> obtenerTodosLosCursos() {
        List<Curso> cursos = cursoService.obtenerTodosLosCursos();
        List<CursoResponseDTO> cursosDTO = cursos.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(cursosDTO);
    }
    
    @Operation(
        summary = "Obtener cursos activos",
        description = "Recupera únicamente los cursos que están marcados como activos."
    )
    @ApiResponse(responseCode = "200", description = "Lista de cursos activos obtenida")
    @GetMapping("/activos")
    public ResponseEntity<List<CursoResponseDTO>> obtenerCursosActivos() {
        List<Curso> cursos = cursoService.obtenerCursosActivos();
        List<CursoResponseDTO> cursosDTO = cursos.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(cursosDTO);
    }
    
    @Operation(
        summary = "Obtener curso por ID",
        description = "Busca y retorna un curso específico por su identificador único."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Curso encontrado",
            content = @Content(schema = @Schema(implementation = CursoResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado",
            content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> obtenerCursoPorId(
            @Parameter(description = "ID del curso a buscar", required = true)
            @PathVariable Integer id) {
        Optional<Curso> curso = cursoService.obtenerCursoPorId(id);
        return curso.map(c -> ResponseEntity.ok(convertirADTO(c)))
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(
        summary = "Obtener curso por código",
        description = "Busca y retorna un curso específico mediante su código único."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Curso encontrado"),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<CursoResponseDTO> obtenerCursoPorCodigo(
            @Parameter(description = "Código del curso", example = "MAT101", required = true)
            @PathVariable String codigo) {
        Optional<Curso> curso = cursoService.obtenerCursoPorCodigo(codigo);
        return curso.map(c -> ResponseEntity.ok(convertirADTO(c)))
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(
        summary = "Buscar cursos por nombre",
        description = "Realiza una búsqueda de cursos que contengan el texto especificado en su nombre. " +
                     "La búsqueda no es sensible a mayúsculas/minúsculas."
    )
    @ApiResponse(responseCode = "200", description = "Búsqueda completada")
    @GetMapping("/buscar")
    public ResponseEntity<List<CursoResponseDTO>> buscarCursosPorNombre(
            @Parameter(description = "Texto a buscar en el nombre del curso", example = "Matemáticas")
            @RequestParam String nombre) {
        List<Curso> cursos = cursoService.buscarCursosPorNombre(nombre);
        List<CursoResponseDTO> cursosDTO = cursos.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(cursosDTO);
    }
    
    @Operation(
        summary = "Obtener cursos por docente",
        description = "Recupera todos los cursos asignados a un docente específico."
    )
    @ApiResponse(responseCode = "200", description = "Lista de cursos del docente")
    @GetMapping("/docente/{idDocente}")
    public ResponseEntity<List<CursoResponseDTO>> obtenerCursosPorDocente(
            @Parameter(description = "ID del docente", required = true)
            @PathVariable Integer idDocente) {
        List<Curso> cursos = cursoService.obtenerCursosPorIdDocente(idDocente);
        List<CursoResponseDTO> cursosDTO = cursos.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(cursosDTO);
    }
    
    // Obtener cursos activos por docente
    @Operation(
        summary = "Obtener cursos activos por docente",
        description = "Obtiene una lista de cursos que están activos y asignados a un docente específico."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cursos activos obtenidos")
    })
    @GetMapping("/docente/{idDocente}/activos")
    public ResponseEntity<List<CursoResponseDTO>> obtenerCursosActivosPorDocente(@PathVariable Integer idDocente) {
        List<Curso> cursos = cursoService.obtenerCursosActivosPorDocente(idDocente);
        List<CursoResponseDTO> cursosDTO = cursos.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(cursosDTO);
    }
    
    // Obtener cursos por créditos
    @Operation(
        summary = "Obtener cursos por créditos",
        description = "Obtiene una lista de cursos que tienen un número específico de créditos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cursos obtenidos")
    })
    @GetMapping("/creditos/{creditos}")
    public ResponseEntity<List<CursoResponseDTO>> obtenerCursosPorCreditos(@PathVariable Integer creditos) {
        List<Curso> cursos = cursoService.obtenerCursosPorCreditos(creditos);
        List<CursoResponseDTO> cursosDTO = cursos.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(cursosDTO);
    }
    
    // Obtener cursos con mínimo de créditos
    @Operation(
        summary = "Obtener cursos con mínimo de créditos",
        description = "Obtiene una lista de cursos que tienen al menos un número específico de créditos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cursos obtenidos")
    })
    @GetMapping("/creditos/minimo/{minCreditos}")
    public ResponseEntity<List<CursoResponseDTO>> obtenerCursosConMinCreditos(@PathVariable Integer minCreditos) {
        List<Curso> cursos = cursoService.obtenerCursosConMinCreditos(minCreditos);
        List<CursoResponseDTO> cursosDTO = cursos.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(cursosDTO);
    }
    
    @Operation(
        summary = "Asignar docente a un curso",
        description = "Asocia un docente existente con un curso específico."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Docente asignado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Docente no encontrado"),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    @PutMapping("/{idCurso}/docente/{idDocente}")
    public ResponseEntity<CursoResponseDTO> asignarDocente(
            @Parameter(description = "ID del curso", required = true) @PathVariable Integer idCurso, 
            @Parameter(description = "ID del docente a asignar", required = true) @PathVariable Integer idDocente) {
        try {
            Optional<Docente> docente = docenteService.obtenerDocentePorId(idDocente);
            if (docente.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            Curso curso = cursoService.asignarDocente(idCurso, docente.get());
            return ResponseEntity.ok(convertirADTO(curso));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(
        summary = "Actualizar un curso",
        description = "Actualiza la información de un curso existente."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Curso actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> actualizarCurso(
            @Parameter(description = "ID del curso a actualizar", required = true) @PathVariable Integer id, 
            @Valid @RequestBody CrearCursoDTO cursoDTO) {
        try {
            Curso cursoActualizado = Curso.builder()
                .nombre(cursoDTO.getNombre())
                .codigoCurso(cursoDTO.getCodigoCurso())
                .descripcion(cursoDTO.getDescripcion())
                .creditos(cursoDTO.getCreditos())
                .build();
            
            // Si se proporciona ID de docente, asociarlo
            if (cursoDTO.getIdDocente() != null) {
                Optional<Docente> docente = docenteService.obtenerDocentePorId(cursoDTO.getIdDocente());
                docente.ifPresent(cursoActualizado::setDocente);
            }
            
            Curso curso = cursoService.actualizarCurso(id, cursoActualizado);
            return ResponseEntity.ok(convertirADTO(curso));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(
        summary = "Desactivar curso",
        description = "Marca un curso como inactivo sin eliminarlo de la base de datos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Curso desactivado"),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<CursoResponseDTO> desactivarCurso(
            @Parameter(description = "ID del curso", required = true) @PathVariable Integer id) {
        try {
            Curso curso = cursoService.desactivarCurso(id);
            return ResponseEntity.ok(convertirADTO(curso));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Activar curso
    @Operation(
        summary = "Activar curso",
        description = "Marca un curso como activo sin eliminarlo de la base de datos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Curso activado"),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    @PutMapping("/{id}/activar")
    public ResponseEntity<CursoResponseDTO> activarCurso(@PathVariable Integer id) {
        try {
            Curso curso = cursoService.activarCurso(id);
            return ResponseEntity.ok(convertirADTO(curso));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(
        summary = "Eliminar curso",
        description = "Elimina permanentemente un curso del sistema. Esta operación no se puede deshacer."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Curso eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCurso(
            @Parameter(description = "ID del curso a eliminar", required = true) @PathVariable Integer id) {
        try {
            cursoService.eliminarCurso(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Obtener estadísticas por docente
    @Operation(
        summary = "Obtener estadísticas por docente",
        description = "Obtiene estadísticas relacionadas con los cursos agrupados por docente."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas")
    })
    @GetMapping("/estadisticas/docente")
    public ResponseEntity<List<Object[]>> obtenerEstadisticasPorDocente() {
        List<Object[]> estadisticas = cursoService.obtenerEstadisticasPorDocente();
        return ResponseEntity.ok(estadisticas);
    }
    
    // Método de conversión
    private CursoResponseDTO convertirADTO(Curso curso) {
        return CursoResponseDTO.builder()
            .idCurso(curso.getIdCurso())
            .nombre(curso.getNombre())
            .codigoCurso(curso.getCodigoCurso())
            .descripcion(curso.getDescripcion())
            .creditos(curso.getCreditos())
            .activo(curso.getActivo())
            .idDocente(curso.getDocente() != null ? curso.getDocente().getIdDocente() : null)
            .nombreDocente(curso.getDocente() != null ? curso.getDocente().getNombres() : null)
            .apellidosDocente(curso.getDocente() != null ? curso.getDocente().getApellidos() : null)
            .especialidadDocente(curso.getDocente() != null ? curso.getDocente().getEspecialidad() : null)
            .build();
    }
}