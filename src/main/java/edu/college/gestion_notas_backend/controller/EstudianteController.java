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

import edu.college.gestion_notas_backend.dto.request.ActualizarEstudianteConFotoDTO;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Estudiantes", description = "API para la gestión de estudiantes")
@RestController
@RequestMapping("/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {

    private final EstudianteService estudianteService;
    private final UsuarioService usuarioService;
    private final FileStorageService fileStorageService;
    
    @Operation(
        summary = "Crear estudiante completo",
        description = "Crea un nuevo estudiante junto con su usuario asociado en una sola operación. " +
                     "Genera automáticamente el código de estudiante si no se proporciona."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Estudiante creado exitosamente",
            content = @Content(schema = @Schema(implementation = EstudianteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o email ya registrado",
            content = @Content)
    })
    @PostMapping(value = "/completo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // @PostMapping("/completo")
    public ResponseEntity<EstudianteResponseDTO> crearEstudianteCompleto(
            @Valid @ModelAttribute CrearEstudianteCompletoDTO dto) {
        // Log para debug
        System.out.println("=== Crear Estudiante Completo ===");
        System.out.println("Email: " + dto.getEmail());
        System.out.println("Nombres: " + dto.getNombres());
        System.out.println("Apellidos: " + dto.getApellidos());
        System.out.println("Teléfono: " + dto.getTelefono());
        System.out.println("Foto: " + (dto.getFoto() != null ? dto.getFoto().getOriginalFilename() : "null"));
        
        // 1. Crear usuario primero
        Usuario usuario = Usuario.builder()
                .email(dto.getEmail())
                .password(dto.getPassword()) // Se encriptará en el servicio
                .rol(Usuario.Rol.ESTUDIANTE)
                .build();

        Usuario usuarioCreado = usuarioService.crearUsuario(usuario);
        System.out.println("Usuario creado con ID: " + usuarioCreado.getIdUsuario());

        // 2. Generar código si no viene
        String codigo = dto.getCodigoEstudiante();
        if (codigo == null || codigo.trim().isEmpty()) {
            codigo = estudianteService.generarCodigoEstudiante();
            System.out.println("Código generado: " + codigo);
        }

        // 3. Procesar la foto si existe
        String rutaFoto = null;
        if (dto.getFoto() != null && !dto.getFoto().isEmpty()) {
            rutaFoto = fileStorageService.storeFile(dto.getFoto(), "estudiantes");
            System.out.println("Foto guardada en: " + rutaFoto);
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
        System.out.println("Estudiante creado con ID: " + estudianteCreado.getIdEstudiante());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertirADTO(estudianteCreado));
    }

    @Operation(
        summary = "Crear estudiante",
        description = "Crea un nuevo estudiante asociado a un usuario existente."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Estudiante creado exitosamente"),
        @ApiResponse(responseCode = "409", description = "Conflicto - Código de estudiante duplicado")
    })
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

    @Operation(
        summary = "Obtener todos los estudiantes",
        description = "Recupera la lista completa de estudiantes registrados."
    )
    @ApiResponse(responseCode = "200", description = "Lista de estudiantes obtenida")
    @GetMapping
    public ResponseEntity<List<EstudianteResponseDTO>> obtenerTodosLosEstudiantes() {
        List<Estudiante> estudiantes = estudianteService.obtenerTodosLosEstudiantes();
        List<EstudianteResponseDTO> estudiantesDTO = estudiantes.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(estudiantesDTO);
    }

    @Operation(
        summary = "Obtener estudiante por ID",
        description = "Busca un estudiante específico por su identificador."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estudiante encontrado"),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> obtenerEstudiantePorId(
            @Parameter(description = "ID del estudiante", required = true) @PathVariable Integer id) {
        Optional<Estudiante> estudiante = estudianteService.obtenerEstudiantePorId(id);
        return estudiante.map(e -> ResponseEntity.ok(convertirADTO(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener estudiante por código
    @Operation(
        summary = "Obtener estudiante por código",
        description = "Recupera un estudiante específico utilizando su código único."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estudiante encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<EstudianteResponseDTO> obtenerEstudiantePorCodigo(@PathVariable String codigo) {
        Optional<Estudiante> estudiante = estudianteService.obtenerEstudiantePorCodigo(codigo);
        return estudiante.map(e -> ResponseEntity.ok(convertirADTO(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener estudiante por ID de usuario
    @Operation(
        summary = "Obtener estudiante por ID de usuario",
        description = "Recupera un estudiante específico utilizando el ID de su usuario asociado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estudiante encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<EstudianteResponseDTO> obtenerEstudiantePorIdUsuario(@PathVariable Integer idUsuario) {
        Optional<Estudiante> estudiante = estudianteService.obtenerEstudiantePorIdUsuario(idUsuario);
        return estudiante.map(e -> ResponseEntity.ok(convertirADTO(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar estudiantes por nombre
    @Operation(
        summary = "Buscar estudiantes por nombre",
        description = "Busca estudiantes cuyo nombre coincida con el término proporcionado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estudiantes encontrados exitosamente"),
        @ApiResponse(responseCode = "404", description = "Estudiantes no encontrados")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<EstudianteResponseDTO>> buscarEstudiantesPorNombre(@RequestParam String nombre) {
        List<Estudiante> estudiantes = estudianteService.buscarEstudiantesPorNombre(nombre);
        List<EstudianteResponseDTO> estudiantesDTO = estudiantes.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(estudiantesDTO);
    }

    // Obtener estudiantes por distrito
    @Operation(
        summary = "Obtener estudiantes por distrito",
        description = "Recupera la lista de estudiantes que pertenecen a un distrito específico."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estudiantes encontrados exitosamente"),
        @ApiResponse(responseCode = "404", description = "Estudiantes no encontrados")
    })
    @GetMapping("/distrito/{distrito}")
    public ResponseEntity<List<EstudianteResponseDTO>> obtenerEstudiantesPorDistrito(@PathVariable String distrito) {
        List<Estudiante> estudiantes = estudianteService.obtenerEstudiantesPorDistrito(distrito);
        List<EstudianteResponseDTO> estudiantesDTO = estudiantes.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(estudiantesDTO);
    }

    @Operation(
        summary = "Actualizar perfil del estudiante",
        description = "Actualiza información personal del estudiante incluyendo datos de contacto."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    @PutMapping(value = "/{id}/perfil", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EstudianteResponseDTO> actualizarPerfilEstudiante(
            @Parameter(description = "ID del estudiante", required = true) @PathVariable Integer id,
            @Valid @ModelAttribute ActualizarPerfilEstudianteDTO perfilDTO) {
        try {
            // Obtener estudiante actual
            Estudiante estudianteActual = estudianteService.obtenerEstudiantePorId(id)
                    .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con ID: " + id));

            // Procesar foto si existe
            String rutaFoto = estudianteActual.getFoto(); // Mantener foto actual por defecto
            if (perfilDTO.getFoto() != null && !perfilDTO.getFoto().isEmpty()) {
                // Eliminar foto anterior si existe
                if (rutaFoto != null && !rutaFoto.isEmpty()) {
                    try {
                        fileStorageService.deleteFile(rutaFoto, "estudiantes");
                        System.out.println("Foto anterior eliminada: " + rutaFoto);
                    } catch (Exception e) {
                        System.err.println("No se pudo eliminar foto anterior: " + rutaFoto);
                    }
                }
                // Guardar nueva foto
                rutaFoto = fileStorageService.storeFile(perfilDTO.getFoto(), "estudiantes");
                System.out.println("Nueva foto guardada en: " + rutaFoto);
            }
                    Estudiante estudiante = estudianteService.actualizarPerfilEstudiante(
                    id, perfilDTO.getNombres(), perfilDTO.getApellidos(), perfilDTO.getTelefono(),
                    perfilDTO.getDireccion(), perfilDTO.getDistrito(), rutaFoto,
                    perfilDTO.getFechaNacimiento(), perfilDTO.getEmail());
            return ResponseEntity.ok(convertirADTO(estudiante));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Actualizar estudiante completo (multipart con foto o sin foto)
    
    // Actualizar estudiante completo
    @Operation(
        summary = "Actualizar un estudiante",
        description = "Actualiza la información de un estudiante existente."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estudiante actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // @PutMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> actualizarEstudiante(
            @PathVariable Integer id,
            @ModelAttribute ActualizarEstudianteConFotoDTO estudianteDTO) {
        try {
            // Obtener estudiante actual
            Estudiante estudianteActual = estudianteService.obtenerEstudiantePorId(id)
                    .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con ID: " + id));

            System.out.println("=== Actualizar Estudiante ===");
            System.out.println("ID: " + id);
            System.out.println("Nombres: " + estudianteDTO.getNombres());
            System.out.println("Foto: " + (estudianteDTO.getFoto() != null ? estudianteDTO.getFoto().getOriginalFilename() : "null"));

            // Procesar foto si existe
            String rutaFoto = estudianteActual.getFoto(); // Mantener foto actual por defecto
            if (estudianteDTO.getFoto() != null && !estudianteDTO.getFoto().isEmpty()) {
                // Eliminar foto anterior si existe
                if (rutaFoto != null && !rutaFoto.isEmpty()) {
                    try {
                        fileStorageService.deleteFile(rutaFoto, "estudiantes");
                        System.out.println("Foto anterior eliminada: " + rutaFoto);
                    } catch (Exception e) {
                        System.err.println("No se pudo eliminar foto anterior: " + rutaFoto);
                    }
                }
                // Guardar nueva foto
                rutaFoto = fileStorageService.storeFile(estudianteDTO.getFoto(), "estudiantes");
                System.out.println("Nueva foto guardada en: " + rutaFoto);
            }

            // Construir estudiante actualizado
            Estudiante estudianteActualizado = Estudiante.builder()
                    .nombres(estudianteDTO.getNombres())
                    .apellidos(estudianteDTO.getApellidos())
                    .telefono(estudianteDTO.getTelefono())
                    .direccion(estudianteDTO.getDireccion())
                    .distrito(estudianteDTO.getDistrito())
                    .foto(rutaFoto)
                    .fechaNacimiento(estudianteDTO.getFechaNacimiento())
                    .codigoEstudiante(estudianteDTO.getCodigoEstudiante())
                    .build();

            Estudiante estudiante = estudianteService.actualizarEstudiante(id, estudianteActualizado);
            System.out.println("Estudiante actualizado con ID: " + estudiante.getIdEstudiante());
            return ResponseEntity.ok(convertirADTO(estudiante));
        } catch (RuntimeException e) {
            System.err.println("Error al actualizar estudiante: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    // Actualizar estudiante sin foto (JSON)
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EstudianteResponseDTO> actualizarEstudianteSinFoto(
            @PathVariable Integer id,
            @Valid @RequestBody ActualizarEstudianteDTO estudianteDTO) {
        try {
            System.out.println("=== Actualizar Estudiante (JSON) ===");
            System.out.println("ID: " + id);
            System.out.println("Nombres: " + estudianteDTO.getNombres());
            
            Estudiante estudianteActualizado = Estudiante.builder()
                    .nombres(estudianteDTO.getNombres())
                    .apellidos(estudianteDTO.getApellidos())
                    .telefono(estudianteDTO.getTelefono())
                    .direccion(estudianteDTO.getDireccion())
                    .distrito(estudianteDTO.getDistrito())
                    .foto(estudianteDTO.getFoto())
                    .fechaNacimiento(estudianteDTO.getFechaNacimiento())
                    .codigoEstudiante(estudianteDTO.getCodigoEstudiante())
                    .build();

            Estudiante estudiante = estudianteService.actualizarEstudiante(id, estudianteActualizado);
            System.out.println("Estudiante actualizado (JSON) con ID: " + estudiante.getIdEstudiante());
            return ResponseEntity.ok(convertirADTO(estudiante));
        } catch (RuntimeException e) {
            System.err.println("Error al actualizar estudiante (JSON): " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Eliminar estudiante",
        description = "Elimina permanentemente un estudiante del sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Estudiante eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEstudiante(
            @Parameter(description = "ID del estudiante", required = true) @PathVariable Integer id) {
        try {
            estudianteService.eliminarEstudiante(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Obtener estadísticas por distrito
    @Operation(
        summary = "Obtener estadísticas por distrito",
        description = "Recupera estadísticas de estudiantes agrupadas por distrito."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente"),
        @ApiResponse(responseCode = "404", description = "Estadísticas no encontradas")
    })
    @GetMapping("/estadisticas/distrito")
    public ResponseEntity<List<Object[]>> obtenerEstadisticasPorDistrito() {
        List<Object[]> estadisticas = estudianteService.obtenerEstadisticasPorDistrito();
        return ResponseEntity.ok(estadisticas);
    }

    // Método de conversión
    private EstudianteResponseDTO convertirADTO(Estudiante estudiante) {
        // Construir URL completa de la foto si existe
        String fotoUrl = null;
        if (estudiante.getFoto() != null && !estudiante.getFoto().isEmpty()) {
            fotoUrl = "/uploads/estudiantes/" + estudiante.getFoto();
        }
        
        return EstudianteResponseDTO.builder()
                .idEstudiante(estudiante.getIdEstudiante())
                .nombres(estudiante.getNombres())
                .apellidos(estudiante.getApellidos())
                .telefono(estudiante.getTelefono())
                .direccion(estudiante.getDireccion())
                .distrito(estudiante.getDistrito())
                .foto(fotoUrl)
                .fechaNacimiento(estudiante.getFechaNacimiento())
                .codigoEstudiante(estudiante.getCodigoEstudiante())
                .email(estudiante.getUsuario().getEmail() != null ? estudiante.getUsuario().getEmail() : null)
                .rolUsuario(estudiante.getUsuario() != null ? estudiante.getUsuario().getRol().toString() : null)
                .usuarioActivo(estudiante.getUsuario() != null ? estudiante.getUsuario().getActivo() : null)
                .build();
    }
}