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

import edu.college.gestion_notas_backend.dto.request.ActualizarDocenteDTO;
import edu.college.gestion_notas_backend.dto.request.ActualizarPerfilDocenteDTO;
import edu.college.gestion_notas_backend.dto.request.CrearDocenteCompletoDTO;
import edu.college.gestion_notas_backend.dto.request.CrearDocenteDTO;
import edu.college.gestion_notas_backend.dto.response.DocenteResponseDTO;
import edu.college.gestion_notas_backend.model.Docente;
import edu.college.gestion_notas_backend.model.Usuario;
import edu.college.gestion_notas_backend.service.DocenteService;
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

@Tag(name = "Docentes", description = "API para la gestión de docentes")
@RestController
@RequestMapping("/docentes")
@RequiredArgsConstructor
public class DocenteController {

    private final DocenteService docenteService;
    private final UsuarioService usuarioService;

    @Operation(
        summary = "Crear docente completo",
        description = "Crea un nuevo docente junto con su usuario asociado en una sola operación. " +
                     "Genera automáticamente el código de docente si no se proporciona."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Docente creado exitosamente",
            content = @Content(schema = @Schema(implementation = DocenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o email ya registrado",
            content = @Content)
    })
    @PostMapping("/completo")
    public ResponseEntity<DocenteResponseDTO> crearDocenteCompleto(
            @Valid @RequestBody CrearDocenteCompletoDTO dto) {
        try {
            // 1. Crear usuario primero
            Usuario usuario = Usuario.builder()
                    .email(dto.getEmail())
                    .password(dto.getPassword()) // Se encriptará en el servicio
                    .rol(Usuario.Rol.DOCENTE)
                    .build();

            Usuario usuarioCreado = usuarioService.crearUsuario(usuario);

            // 2. Generar código si no viene
            String codigo = dto.getCodigoDocente();
            if (codigo == null || codigo.trim().isEmpty()) {
                codigo = docenteService.generarCodigoDocente();
            }

            // 3. Crear docente
            Docente docente = Docente.builder()
                    .usuario(usuarioCreado)
                    .nombres(dto.getNombres())
                    .apellidos(dto.getApellidos())
                    .codigoDocente(codigo)
                    .telefono(dto.getTelefono())
                    .direccion(dto.getDireccion())
                    .distrito(dto.getDistrito())
                    .foto(dto.getFoto())
                    .especialidad(dto.getEspecialidad())
                    .fechaContratacion(dto.getFechaContratacion())
                    .build();

            Docente docenteCreado = docenteService.crearDocente(docente);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(convertirADTO(docenteCreado));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
        summary = "Crear docente",
        description = "Crea un nuevo docente asociado a un usuario existente. " +
                     "Requiere el ID de un usuario previamente creado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Docente creado exitosamente"),
        @ApiResponse(responseCode = "409", description = "Conflicto - Código de docente duplicado")
    })
    @PostMapping
    public ResponseEntity<DocenteResponseDTO> crearDocente(@Valid @RequestBody CrearDocenteDTO crearDocenteDTO) {
        try {
            // Buscar usuario por ID
            Usuario usuario = usuarioService.obtenerUsuarioPorId(crearDocenteDTO.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException(
                            "Usuario no encontrado con ID: " + crearDocenteDTO.getIdUsuario()));

            Docente docente = Docente.builder()
                    .usuario(usuario)
                    .nombres(crearDocenteDTO.getNombres())
                    .apellidos(crearDocenteDTO.getApellidos())
                    .telefono(crearDocenteDTO.getTelefono())
                    .direccion(crearDocenteDTO.getDireccion())
                    .distrito(crearDocenteDTO.getDistrito())
                    .foto(crearDocenteDTO.getFoto())
                    .especialidad(crearDocenteDTO.getEspecialidad())
                    .fechaContratacion(crearDocenteDTO.getFechaContratacion())
                    .codigoDocente(
                            crearDocenteDTO.getCodigoDocente() != null ? crearDocenteDTO.getCodigoDocente()
                                    : docenteService.generarCodigoDocente())
                    .build();

            Docente docenteCreado = docenteService.crearDocente(docente);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(docenteCreado));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Operation(
        summary = "Obtener todos los docentes",
        description = "Recupera la lista completa de docentes registrados en el sistema."
    )
    @ApiResponse(responseCode = "200", description = "Lista de docentes obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<DocenteResponseDTO>> obtenerTodosLosDocentes() {
        List<Docente> docentes = docenteService.obtenerTodosLosDocentes();
        List<DocenteResponseDTO> docentesDTO = docentes.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(docentesDTO);
    }

    @Operation(
        summary = "Obtener docentes activos",
        description = "Recupera únicamente los docentes con usuario activo."
    )
    @ApiResponse(responseCode = "200", description = "Lista de docentes activos")
    @GetMapping("/activos")
    public ResponseEntity<List<DocenteResponseDTO>> obtenerDocentesActivos() {
        List<Docente> docentes = docenteService.obtenerDocentesActivos();
        List<DocenteResponseDTO> docentesDTO = docentes.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(docentesDTO);
    }

    @Operation(
        summary = "Obtener docente por ID",
        description = "Busca y retorna un docente específico por su identificador."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Docente encontrado"),
        @ApiResponse(responseCode = "404", description = "Docente no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DocenteResponseDTO> obtenerDocentePorId(
            @Parameter(description = "ID del docente", required = true) @PathVariable Integer id) {
        Optional<Docente> docente = docenteService.obtenerDocentePorId(id);
        return docente.map(d -> ResponseEntity.ok(convertirADTO(d)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Obtener docente por ID de usuario",
        description = "Busca un docente asociado a un ID de usuario específico."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Docente encontrado"),
        @ApiResponse(responseCode = "404", description = "No existe docente para ese usuario")
    })
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<DocenteResponseDTO> obtenerDocentePorIdUsuario(
            @Parameter(description = "ID del usuario", required = true) @PathVariable Integer idUsuario) {
        Optional<Docente> docente = docenteService.obtenerDocentePorIdUsuario(idUsuario);
        return docente.map(d -> ResponseEntity.ok(convertirADTO(d)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Buscar docentes por nombre",
        description = "Realiza una búsqueda de docentes por nombres o apellidos. " +
                     "La búsqueda es insensible a mayúsculas/minúsculas."
    )
    @ApiResponse(responseCode = "200", description = "Búsqueda completada")
    @GetMapping("/buscar")
    public ResponseEntity<List<DocenteResponseDTO>> buscarDocentesPorNombre(
            @Parameter(description = "Texto a buscar en nombres o apellidos", example = "Juan") 
            @RequestParam String nombre) {
        List<Docente> docentes = docenteService.buscarDocentesPorNombre(nombre);
        List<DocenteResponseDTO> docentesDTO = docentes.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(docentesDTO);
    }

    @Operation(
        summary = "Obtener docentes por especialidad",
        description = "Recupera todos los docentes que tienen una especialidad específica."
    )
    @ApiResponse(responseCode = "200", description = "Lista de docentes con esa especialidad")
    @GetMapping("/especialidad/{especialidad}")
    public ResponseEntity<List<DocenteResponseDTO>> obtenerDocentesPorEspecialidad(
            @Parameter(description = "Especialidad del docente", example = "Matemáticas") 
            @PathVariable String especialidad) {
        List<Docente> docentes = docenteService.obtenerDocentesPorEspecialidad(especialidad);
        List<DocenteResponseDTO> docentesDTO = docentes.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(docentesDTO);
    }

    // Obtener docentes por distrito
    @Operation(
        summary = "Obtener docentes por distrito",
        description = "Recupera todos los docentes que pertenecen a un distrito específico."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Docentes obtenidos exitosamente"),
        @ApiResponse(responseCode = "404", description = "Docentes no encontrados")
    })
    @GetMapping("/distrito/{distrito}")
    public ResponseEntity<List<DocenteResponseDTO>> obtenerDocentesPorDistrito(@PathVariable String distrito) {
        List<Docente> docentes = docenteService.obtenerDocentesPorDistrito(distrito);
        List<DocenteResponseDTO> docentesDTO = docentes.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(docentesDTO);
    }

    @Operation(
        summary = "Actualizar perfil del docente",
        description = "Actualiza información personal del docente incluyendo datos de contacto y especialidad."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Docente no encontrado")
    })
    @PutMapping("/{id}/perfil")
    public ResponseEntity<DocenteResponseDTO> actualizarPerfilDocente(
            @Parameter(description = "ID del docente", required = true) @PathVariable Integer id,
            @Valid @RequestBody ActualizarPerfilDocenteDTO perfilDTO) {
        try {
            Docente docente = docenteService.actualizarPerfilDocente(
                    id, perfilDTO.getNombres(), perfilDTO.getApellidos(), perfilDTO.getTelefono(),
                    perfilDTO.getDireccion(), perfilDTO.getDistrito(),
                    perfilDTO.getFoto(), perfilDTO.getEspecialidad(), perfilDTO.getFechaContratacion(),
                    perfilDTO.getEmail());
            return ResponseEntity.ok(convertirADTO(docente));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Actualizar docente completo
    @Operation(
        summary = "Actualizar un docente",
        description = "Actualiza la información de un docente existente."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Docente actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Docente no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DocenteResponseDTO> actualizarDocente(
            @PathVariable Integer id,
            @Valid @RequestBody ActualizarDocenteDTO docenteDTO) {
        try {
            Docente docenteActualizado = Docente.builder()
                    .nombres(docenteDTO.getNombres())
                    .apellidos(docenteDTO.getApellidos())
                    .telefono(docenteDTO.getTelefono())
                    .direccion(docenteDTO.getDireccion())
                    .distrito(docenteDTO.getDistrito())
                    .foto(docenteDTO.getFoto())
                    .especialidad(docenteDTO.getEspecialidad())
                    .fechaContratacion(docenteDTO.getFechaContratacion())
                    .codigoDocente(docenteDTO.getCodigoDocente())
                    // ❌ NO establecer usuario - se mantiene el original
                    .build();

            Docente docente = docenteService.actualizarDocente(id, docenteActualizado);
            return ResponseEntity.ok(convertirADTO(docente));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Eliminar docente",
        description = "Elimina permanentemente un docente del sistema. Esta acción no se puede deshacer."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Docente eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Docente no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDocente(
            @Parameter(description = "ID del docente a eliminar", required = true) @PathVariable Integer id) {
        try {
            docenteService.eliminarDocente(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Obtener estadísticas por especialidad
    @Operation(
        summary = "Obtener estadísticas por especialidad",
        description = "Obtiene estadísticas relacionadas con los docentes agrupados por especialidad."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas"),
        @ApiResponse(responseCode = "404", description = "Especialidad no encontrada")
    })
    @GetMapping("/estadisticas/especialidad")
    public ResponseEntity<List<Object[]>> obtenerEstadisticasPorEspecialidad() {
        List<Object[]> estadisticas = docenteService.obtenerEstadisticasPorEspecialidad();
        return ResponseEntity.ok(estadisticas);
    }

    // Método de conversión
    private DocenteResponseDTO convertirADTO(Docente docente) {
        return DocenteResponseDTO.builder()
                .idDocente(docente.getIdDocente())
                .nombres(docente.getNombres())
                .apellidos(docente.getApellidos())
                .telefono(docente.getTelefono())
                .direccion(docente.getDireccion())
                .distrito(docente.getDistrito())
                .foto(docente.getFoto())
                .especialidad(docente.getEspecialidad())
                .fechaContratacion(docente.getFechaContratacion())
                .codigoDocente(docente.getCodigoDocente())
                .email(docente.getUsuario() != null ? docente.getUsuario().getEmail() : null)
                .rolUsuario(docente.getUsuario() != null ? docente.getUsuario().getRol().toString() : null)
                .usuarioActivo(docente.getUsuario() != null ? docente.getUsuario().getActivo() : null)
                .build();
    }
}