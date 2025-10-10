package edu.college.gestion_notas_backend.controller;

import edu.college.gestion_notas_backend.dto.request.CrearNotaDTO;
import edu.college.gestion_notas_backend.dto.response.NotaResponseDTO;
import edu.college.gestion_notas_backend.model.Nota;
import edu.college.gestion_notas_backend.model.Estudiante;
import edu.college.gestion_notas_backend.model.Curso;
import edu.college.gestion_notas_backend.service.NotaService;
import edu.college.gestion_notas_backend.service.EstudianteService;
import edu.college.gestion_notas_backend.service.CursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notas")
@RequiredArgsConstructor
public class NotaController {
    
    private final NotaService notaService;
    private final EstudianteService estudianteService;
    private final CursoService cursoService;
    
    // Crear nota
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
    @GetMapping
    public ResponseEntity<List<NotaResponseDTO>> obtenerTodasLasNotas() {
        List<Nota> notas = notaService.obtenerTodasLasNotas();
        List<NotaResponseDTO> notasDTO = notas.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(notasDTO);
    }
    
    // Obtener nota por ID
    @GetMapping("/{id}")
    public ResponseEntity<NotaResponseDTO> obtenerNotaPorId(@PathVariable Integer id) {
        Optional<Nota> nota = notaService.obtenerNotaPorId(id);
        return nota.map(n -> ResponseEntity.ok(convertirADTO(n)))
                  .orElse(ResponseEntity.notFound().build());
    }
    
    // Obtener notas por estudiante
    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<List<NotaResponseDTO>> obtenerNotasPorEstudiante(@PathVariable Integer idEstudiante) {
        List<Nota> notas = notaService.obtenerNotasPorIdEstudiante(idEstudiante);
        List<NotaResponseDTO> notasDTO = notas.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(notasDTO);
    }
    
    // Obtener notas por curso
    @GetMapping("/curso/{idCurso}")
    public ResponseEntity<List<NotaResponseDTO>> obtenerNotasPorCurso(@PathVariable Integer idCurso) {
        List<Nota> notas = notaService.obtenerNotasPorIdCurso(idCurso);
        List<NotaResponseDTO> notasDTO = notas.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(notasDTO);
    }
    
    // Obtener notas por tipo de evaluación
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
    
    // Calcular promedio por estudiante
    @GetMapping("/promedio/estudiante/{idEstudiante}")
    public ResponseEntity<Double> calcularPromedioPorEstudiante(@PathVariable Integer idEstudiante) {
        Double promedio = notaService.calcularPromedioPorEstudiante(idEstudiante);
        return ResponseEntity.ok(promedio);
    }
    
    // Calcular promedio por curso
    @GetMapping("/promedio/curso/{idCurso}")
    public ResponseEntity<Double> calcularPromedioPorCurso(@PathVariable Integer idCurso) {
        Double promedio = notaService.calcularPromedioPorCurso(idCurso);
        return ResponseEntity.ok(promedio);
    }
    
    // Calcular promedio por estudiante y curso
    @GetMapping("/promedio/estudiante/{idEstudiante}/curso/{idCurso}")
    public ResponseEntity<Double> calcularPromedioPorEstudianteYCurso(
            @PathVariable Integer idEstudiante, 
            @PathVariable Integer idCurso) {
        Double promedio = notaService.calcularPromedioPorEstudianteYCurso(idEstudiante, idCurso);
        return ResponseEntity.ok(promedio);
    }
    
    // Obtener estado académico
    @GetMapping("/estado/estudiante/{idEstudiante}/curso/{idCurso}")
    public ResponseEntity<String> obtenerEstadoAcademico(
            @PathVariable Integer idEstudiante, 
            @PathVariable Integer idCurso) {
        String estado = notaService.obtenerEstadoAcademico(idEstudiante, idCurso);
        return ResponseEntity.ok(estado);
    }
    
    // Obtener notas aprobatorias
    @GetMapping("/aprobatorias")
    public ResponseEntity<List<NotaResponseDTO>> obtenerNotasAprobatorias() {
        List<Nota> notas = notaService.obtenerNotasAprobatorias();
        List<NotaResponseDTO> notasDTO = notas.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(notasDTO);
    }
    
    // Obtener mejores notas por curso
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
    
    // Actualizar nota
    @PutMapping("/{id}")
    public ResponseEntity<NotaResponseDTO> actualizarNota(
            @PathVariable Integer id, 
            @Valid @RequestBody CrearNotaDTO notaDTO) {
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