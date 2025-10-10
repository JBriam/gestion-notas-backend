package edu.college.gestion_notas_backend.controller;

import edu.college.gestion_notas_backend.dto.request.CrearEstudianteDTO;
import edu.college.gestion_notas_backend.dto.request.ActualizarPerfilEstudianteDTO;
import edu.college.gestion_notas_backend.dto.response.EstudianteResponseDTO;
import edu.college.gestion_notas_backend.model.Estudiante;
import edu.college.gestion_notas_backend.model.Usuario;
import edu.college.gestion_notas_backend.service.EstudianteService;
import edu.college.gestion_notas_backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {
    
    private final EstudianteService estudianteService;
    private final UsuarioService usuarioService;
    
    // Crear estudiante
    @PostMapping
    public ResponseEntity<EstudianteResponseDTO> crearEstudiante(@Valid @RequestBody CrearEstudianteDTO crearEstudianteDTO) {
        try {
            Estudiante estudiante = Estudiante.builder()
                .nombres(crearEstudianteDTO.getNombres())
                .apellidos(crearEstudianteDTO.getApellidos())
                .email(crearEstudianteDTO.getEmail())
                .telefono(crearEstudianteDTO.getTelefono())
                .distrito(crearEstudianteDTO.getDistrito())
                .foto(crearEstudianteDTO.getFoto())
                .fechaNacimiento(crearEstudianteDTO.getFechaNacimiento())
                .codigoEstudiante(crearEstudianteDTO.getCodigoEstudiante() != null ? 
                    crearEstudianteDTO.getCodigoEstudiante() : estudianteService.generarCodigoEstudiante())
                .build();
            
            // Si se proporciona ID de usuario, asociarlo
            if (crearEstudianteDTO.getIdUsuario() != null) {
                Optional<Usuario> usuario = usuarioService.obtenerUsuarioPorId(crearEstudianteDTO.getIdUsuario());
                usuario.ifPresent(estudiante::setUsuario);
            }
            
            Estudiante estudianteCreado = estudianteService.crearEstudiante(estudiante);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(estudianteCreado));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    
    // Obtener todos los estudiantes
    @GetMapping
    public ResponseEntity<List<EstudianteResponseDTO>> obtenerTodosLosEstudiantes() {
        List<Estudiante> estudiantes = estudianteService.obtenerTodosLosEstudiantes();
        List<EstudianteResponseDTO> estudiantesDTO = estudiantes.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(estudiantesDTO);
    }
    
    // Obtener estudiante por ID
    @GetMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> obtenerEstudiantePorId(@PathVariable Integer id) {
        Optional<Estudiante> estudiante = estudianteService.obtenerEstudiantePorId(id);
        return estudiante.map(e -> ResponseEntity.ok(convertirADTO(e)))
                        .orElse(ResponseEntity.notFound().build());
    }
    
    // Obtener estudiante por código
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<EstudianteResponseDTO> obtenerEstudiantePorCodigo(@PathVariable String codigo) {
        Optional<Estudiante> estudiante = estudianteService.obtenerEstudiantePorCodigo(codigo);
        return estudiante.map(e -> ResponseEntity.ok(convertirADTO(e)))
                        .orElse(ResponseEntity.notFound().build());
    }
    
    // Obtener estudiante por ID de usuario
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<EstudianteResponseDTO> obtenerEstudiantePorIdUsuario(@PathVariable Integer idUsuario) {
        Optional<Estudiante> estudiante = estudianteService.obtenerEstudiantePorIdUsuario(idUsuario);
        return estudiante.map(e -> ResponseEntity.ok(convertirADTO(e)))
                        .orElse(ResponseEntity.notFound().build());
    }
    
    // Buscar estudiantes por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<EstudianteResponseDTO>> buscarEstudiantesPorNombre(@RequestParam String nombre) {
        List<Estudiante> estudiantes = estudianteService.buscarEstudiantesPorNombre(nombre);
        List<EstudianteResponseDTO> estudiantesDTO = estudiantes.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(estudiantesDTO);
    }
    
    // Obtener estudiantes por distrito
    @GetMapping("/distrito/{distrito}")
    public ResponseEntity<List<EstudianteResponseDTO>> obtenerEstudiantesPorDistrito(@PathVariable String distrito) {
        List<Estudiante> estudiantes = estudianteService.obtenerEstudiantesPorDistrito(distrito);
        List<EstudianteResponseDTO> estudiantesDTO = estudiantes.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(estudiantesDTO);
    }
    
    // Actualizar perfil del estudiante
    @PutMapping("/{id}/perfil")
    public ResponseEntity<EstudianteResponseDTO> actualizarPerfilEstudiante(
            @PathVariable Integer id, 
            @Valid @RequestBody ActualizarPerfilEstudianteDTO perfilDTO) {
        try {
            Estudiante estudiante = estudianteService.actualizarPerfilEstudiante(
                id, perfilDTO.getTelefono(), perfilDTO.getDistrito(), perfilDTO.getFoto());
            return ResponseEntity.ok(convertirADTO(estudiante));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Actualizar estudiante completo
    @PutMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> actualizarEstudiante(
            @PathVariable Integer id, 
            @Valid @RequestBody CrearEstudianteDTO estudianteDTO) {
        try {
            Estudiante estudianteActualizado = Estudiante.builder()
                .nombres(estudianteDTO.getNombres())
                .apellidos(estudianteDTO.getApellidos())
                .email(estudianteDTO.getEmail())
                .telefono(estudianteDTO.getTelefono())
                .distrito(estudianteDTO.getDistrito())
                .foto(estudianteDTO.getFoto())
                .fechaNacimiento(estudianteDTO.getFechaNacimiento())
                .codigoEstudiante(estudianteDTO.getCodigoEstudiante())
                .build();
            
            Estudiante estudiante = estudianteService.actualizarEstudiante(id, estudianteActualizado);
            return ResponseEntity.ok(convertirADTO(estudiante));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Eliminar estudiante
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEstudiante(@PathVariable Integer id) {
        try {
            estudianteService.eliminarEstudiante(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Obtener estadísticas por distrito
    @GetMapping("/estadisticas/distrito")
    public ResponseEntity<List<Object[]>> obtenerEstadisticasPorDistrito() {
        List<Object[]> estadisticas = estudianteService.obtenerEstadisticasPorDistrito();
        return ResponseEntity.ok(estadisticas);
    }
    
    // Método de conversión
    private EstudianteResponseDTO convertirADTO(Estudiante estudiante) {
        return EstudianteResponseDTO.builder()
            .idEstudiante(estudiante.getIdEstudiante())
            .nombres(estudiante.getNombres())
            .apellidos(estudiante.getApellidos())
            .email(estudiante.getEmail())
            .telefono(estudiante.getTelefono())
            .distrito(estudiante.getDistrito())
            .foto(estudiante.getFoto())
            .fechaNacimiento(estudiante.getFechaNacimiento())
            .codigoEstudiante(estudiante.getCodigoEstudiante())
            .username(estudiante.getUsuario() != null ? estudiante.getUsuario().getUsername() : null)
            .rolUsuario(estudiante.getUsuario() != null ? estudiante.getUsuario().getRol().toString() : null)
            .usuarioActivo(estudiante.getUsuario() != null ? estudiante.getUsuario().getActivo() : null)
            .build();
    }
}