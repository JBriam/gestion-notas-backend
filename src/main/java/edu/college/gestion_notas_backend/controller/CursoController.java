package edu.college.gestion_notas_backend.controller;

import edu.college.gestion_notas_backend.dto.request.CrearCursoDTO;
import edu.college.gestion_notas_backend.dto.response.CursoResponseDTO;
import edu.college.gestion_notas_backend.model.Curso;
import edu.college.gestion_notas_backend.model.Docente;
import edu.college.gestion_notas_backend.service.CursoService;
import edu.college.gestion_notas_backend.service.DocenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cursos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CursoController {
    
    private final CursoService cursoService;
    private final DocenteService docenteService;
    
    // Crear curso
    @PostMapping
    public ResponseEntity<CursoResponseDTO> crearCurso(@Valid @RequestBody CrearCursoDTO crearCursoDTO) {
        try {
            Curso curso = Curso.builder()
                .nombre(crearCursoDTO.getNombre())
                .codigoCurso(crearCursoDTO.getCodigoCurso() != null ? 
                    crearCursoDTO.getCodigoCurso() : cursoService.generarCodigoCurso())
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
    
    // Obtener todos los cursos
    @GetMapping
    public ResponseEntity<List<CursoResponseDTO>> obtenerTodosLosCursos() {
        List<Curso> cursos = cursoService.obtenerTodosLosCursos();
        List<CursoResponseDTO> cursosDTO = cursos.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(cursosDTO);
    }
    
    // Obtener cursos activos
    @GetMapping("/activos")
    public ResponseEntity<List<CursoResponseDTO>> obtenerCursosActivos() {
        List<Curso> cursos = cursoService.obtenerCursosActivos();
        List<CursoResponseDTO> cursosDTO = cursos.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(cursosDTO);
    }
    
    // Obtener curso por ID
    @GetMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> obtenerCursoPorId(@PathVariable Integer id) {
        Optional<Curso> curso = cursoService.obtenerCursoPorId(id);
        return curso.map(c -> ResponseEntity.ok(convertirADTO(c)))
                   .orElse(ResponseEntity.notFound().build());
    }
    
    // Obtener curso por código
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<CursoResponseDTO> obtenerCursoPorCodigo(@PathVariable String codigo) {
        Optional<Curso> curso = cursoService.obtenerCursoPorCodigo(codigo);
        return curso.map(c -> ResponseEntity.ok(convertirADTO(c)))
                   .orElse(ResponseEntity.notFound().build());
    }
    
    // Buscar cursos por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<CursoResponseDTO>> buscarCursosPorNombre(@RequestParam String nombre) {
        List<Curso> cursos = cursoService.buscarCursosPorNombre(nombre);
        List<CursoResponseDTO> cursosDTO = cursos.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(cursosDTO);
    }
    
    // Obtener cursos por docente
    @GetMapping("/docente/{idDocente}")
    public ResponseEntity<List<CursoResponseDTO>> obtenerCursosPorDocente(@PathVariable Integer idDocente) {
        List<Curso> cursos = cursoService.obtenerCursosPorIdDocente(idDocente);
        List<CursoResponseDTO> cursosDTO = cursos.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(cursosDTO);
    }
    
    // Obtener cursos activos por docente
    @GetMapping("/docente/{idDocente}/activos")
    public ResponseEntity<List<CursoResponseDTO>> obtenerCursosActivosPorDocente(@PathVariable Integer idDocente) {
        List<Curso> cursos = cursoService.obtenerCursosActivosPorDocente(idDocente);
        List<CursoResponseDTO> cursosDTO = cursos.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(cursosDTO);
    }
    
    // Obtener cursos por créditos
    @GetMapping("/creditos/{creditos}")
    public ResponseEntity<List<CursoResponseDTO>> obtenerCursosPorCreditos(@PathVariable Integer creditos) {
        List<Curso> cursos = cursoService.obtenerCursosPorCreditos(creditos);
        List<CursoResponseDTO> cursosDTO = cursos.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(cursosDTO);
    }
    
    // Obtener cursos con mínimo de créditos
    @GetMapping("/creditos/minimo/{minCreditos}")
    public ResponseEntity<List<CursoResponseDTO>> obtenerCursosConMinCreditos(@PathVariable Integer minCreditos) {
        List<Curso> cursos = cursoService.obtenerCursosConMinCreditos(minCreditos);
        List<CursoResponseDTO> cursosDTO = cursos.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(cursosDTO);
    }
    
    // Asignar docente a curso
    @PutMapping("/{idCurso}/docente/{idDocente}")
    public ResponseEntity<CursoResponseDTO> asignarDocente(
            @PathVariable Integer idCurso, 
            @PathVariable Integer idDocente) {
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
    
    // Actualizar curso
    @PutMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> actualizarCurso(
            @PathVariable Integer id, 
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
    
    // Desactivar curso
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<CursoResponseDTO> desactivarCurso(@PathVariable Integer id) {
        try {
            Curso curso = cursoService.desactivarCurso(id);
            return ResponseEntity.ok(convertirADTO(curso));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Activar curso
    @PutMapping("/{id}/activar")
    public ResponseEntity<CursoResponseDTO> activarCurso(@PathVariable Integer id) {
        try {
            Curso curso = cursoService.activarCurso(id);
            return ResponseEntity.ok(convertirADTO(curso));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Eliminar curso
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCurso(@PathVariable Integer id) {
        try {
            cursoService.eliminarCurso(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Obtener estadísticas por docente
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