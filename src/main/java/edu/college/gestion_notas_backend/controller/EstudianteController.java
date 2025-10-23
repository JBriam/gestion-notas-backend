package edu.college.gestion_notas_backend.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.college.gestion_notas_backend.dto.request.ActualizarEstudianteDTO;
import edu.college.gestion_notas_backend.dto.request.ActualizarPerfilEstudianteDTO;
import edu.college.gestion_notas_backend.dto.request.CrearEstudianteCompletoDTO;
import edu.college.gestion_notas_backend.dto.request.CrearEstudianteDTO;
import edu.college.gestion_notas_backend.dto.response.EstudianteResponseDTO;
import edu.college.gestion_notas_backend.model.Estudiante;
import edu.college.gestion_notas_backend.model.Usuario;
import edu.college.gestion_notas_backend.service.EstudianteService;
import edu.college.gestion_notas_backend.service.FileStorageService;
import edu.college.gestion_notas_backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {

    private final EstudianteService estudianteService;
    private final UsuarioService usuarioService;
    private final FileStorageService fileStorageService;

    @PostMapping(value = "/completo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EstudianteResponseDTO> crearEstudianteCompleto(
            @Valid @ModelAttribute CrearEstudianteCompletoDTO dto) {
        try {
            // 1. Crear usuario primero
            Usuario usuario = Usuario.builder()
                    .email(dto.getEmail())
                    .password(dto.getPassword()) // Se encriptará en el servicio
                    .rol(Usuario.Rol.ESTUDIANTE)
                    .build();

            Usuario usuarioCreado = usuarioService.crearUsuario(usuario);

            // 2. Generar código si no viene
            String codigo = dto.getCodigoEstudiante();
            if (codigo == null || codigo.trim().isEmpty()) {
                codigo = estudianteService.generarCodigoEstudiante();
            }

            // 3. Procesar la foto si existe
            String rutaFoto = null;
            if (dto.getFoto() != null && !dto.getFoto().isEmpty()) {
                rutaFoto = fileStorageService.storeFile(dto.getFoto());
            }

            // 4. Crear estudiante
            Estudiante estudiante = Estudiante.builder()
                    .usuario(usuarioCreado)
                    .nombres(dto.getNombres())
                    .apellidos(dto.getApellidos())
                    .codigoEstudiante(codigo)
                    .telefono(dto.getTelefono())
                    .direccion(dto.getDireccion())
                    .distrito(dto.getDistrito())
                    .foto(rutaFoto)
                    .fechaNacimiento(dto.getFechaNacimiento())
                    .build();

            Estudiante estudianteCreado = estudianteService.crearEstudiante(estudiante);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(convertirADTO(estudianteCreado));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Crear estudiante
    @PostMapping
    public ResponseEntity<EstudianteResponseDTO> crearEstudiante(
            @Valid @RequestBody CrearEstudianteDTO crearEstudianteDTO) {
        try {
            // Buscar usuario por ID
            Usuario usuario = usuarioService.obtenerUsuarioPorId(crearEstudianteDTO.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException(
                            "Usuario no encontrado con ID: " + crearEstudianteDTO.getIdUsuario()));

            Estudiante estudiante = Estudiante.builder()
                    .usuario(usuario)
                    .nombres(crearEstudianteDTO.getNombres())
                    .apellidos(crearEstudianteDTO.getApellidos())
                    .telefono(crearEstudianteDTO.getTelefono())
                    .direccion(crearEstudianteDTO.getDireccion())
                    .distrito(crearEstudianteDTO.getDistrito())
                    .foto(crearEstudianteDTO.getFoto())
                    .fechaNacimiento(crearEstudianteDTO.getFechaNacimiento())
                    .codigoEstudiante(
                            crearEstudianteDTO.getCodigoEstudiante() != null ? crearEstudianteDTO.getCodigoEstudiante()
                                    : estudianteService.generarCodigoEstudiante())
                    .build();

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
                    id, perfilDTO.getNombres(), perfilDTO.getApellidos(), perfilDTO.getTelefono(),
                    perfilDTO.getDireccion(), perfilDTO.getDistrito(), perfilDTO.getFoto(),
                    perfilDTO.getFechaNacimiento(), perfilDTO.getEmail());
            return ResponseEntity.ok(convertirADTO(estudiante));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Actualizar estudiante completo
    @PutMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> actualizarEstudiante(
            @PathVariable Integer id,
            @Valid @RequestBody ActualizarEstudianteDTO estudianteDTO) {
        try {
            Estudiante estudianteActualizado = Estudiante.builder()
                    .nombres(estudianteDTO.getNombres())
                    .apellidos(estudianteDTO.getApellidos())
                    .telefono(estudianteDTO.getTelefono())
                    .direccion(estudianteDTO.getDireccion())
                    .distrito(estudianteDTO.getDistrito())
                    .foto(estudianteDTO.getFoto())
                    .fechaNacimiento(estudianteDTO.getFechaNacimiento())
                    .codigoEstudiante(estudianteDTO.getCodigoEstudiante())
                    // ❌ NO establecer usuario - se mantiene el original
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
                .telefono(estudiante.getTelefono())
                .direccion(estudiante.getDireccion())
                .distrito(estudiante.getDistrito())
                .foto(estudiante.getFoto())
                .fechaNacimiento(estudiante.getFechaNacimiento())
                .codigoEstudiante(estudiante.getCodigoEstudiante())
                .email(estudiante.getUsuario().getEmail() != null ? estudiante.getUsuario().getEmail() : null)
                .rolUsuario(estudiante.getUsuario() != null ? estudiante.getUsuario().getRol().toString() : null)
                .usuarioActivo(estudiante.getUsuario() != null ? estudiante.getUsuario().getActivo() : null)
                .build();
    }
}