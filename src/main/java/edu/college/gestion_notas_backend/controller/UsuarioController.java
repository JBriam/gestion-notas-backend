package edu.college.gestion_notas_backend.controller;

import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RestController;

import edu.college.gestion_notas_backend.dto.request.CrearUsuarioDTO;
import edu.college.gestion_notas_backend.dto.request.LoginDTO;
import edu.college.gestion_notas_backend.dto.request.RegistroCompletoDTO;
import edu.college.gestion_notas_backend.dto.response.DocenteResponseDTO;
import edu.college.gestion_notas_backend.dto.response.EstudianteResponseDTO;
import edu.college.gestion_notas_backend.dto.response.LoginResponseDTO;
import edu.college.gestion_notas_backend.dto.response.UsuarioResponseDTO;
import edu.college.gestion_notas_backend.model.Docente;
import edu.college.gestion_notas_backend.model.Estudiante;
import edu.college.gestion_notas_backend.model.Usuario;
import edu.college.gestion_notas_backend.service.DocenteService;
import edu.college.gestion_notas_backend.service.EstudianteService;
import edu.college.gestion_notas_backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final EstudianteService estudianteService;
    private final DocenteService docenteService;

    // Login de usuario
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            boolean credencialesValidas = usuarioService.verificarCredenciales(
                    loginDTO.getEmail(), loginDTO.getPassword());

            if (!credencialesValidas) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(LoginResponseDTO.builder()
                                .success(false)
                                .message("Credenciales incorrectas")
                                .build());
            }
            Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorEmail(loginDTO.getEmail());
            Usuario usuario = usuarioOpt.get();

            LoginResponseDTO response = LoginResponseDTO.builder()
                    .success(true)
                    .message("Login exitoso")
                    .usuario(convertirAUsuarioDTO(usuario))
                    .build();

            // Agregar información del perfil según el rol
            if (usuario.getRol() == Usuario.Rol.ESTUDIANTE) {
                estudianteService.obtenerEstudiantePorIdUsuario(usuario.getIdUsuario())
                        .ifPresent(estudiante -> response.setPerfilEstudiante(convertirAEstudianteDTO(estudiante)));
            } else if (usuario.getRol() == Usuario.Rol.DOCENTE || usuario.getRol() == Usuario.Rol.ADMIN) {
                docenteService.obtenerDocentePorIdUsuario(usuario.getIdUsuario())
                        .ifPresent(docente -> response.setPerfilDocente(convertirADocenteDTO(docente)));
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(LoginResponseDTO.builder()
                            .success(false)
                            .message("Error interno del servidor")
                            .build());
        }
    }

    // Crear usuario
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(@Valid @RequestBody CrearUsuarioDTO crearUsuarioDTO) {
        try {
            Usuario usuario = Usuario.builder()
                    .email(crearUsuarioDTO.getEmail())
                    .password(crearUsuarioDTO.getPassword())
                    .rol(Usuario.Rol.valueOf(crearUsuarioDTO.getRol()))
                    .build();

            Usuario usuarioCreado = usuarioService.crearUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertirAUsuarioDTO(usuarioCreado));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    // Crear usuario con perfil completo (para el formulario de registro)
    @PostMapping("/registro-completo")
    public ResponseEntity<?> crearUsuarioCompleto(@Valid @RequestBody RegistroCompletoDTO dto) {
        try {
            // 1. Crear usuario primero
            Usuario usuario = Usuario.builder()// Usar email como username
                    .email(dto.getEmail())
                    .password(dto.getPassword()) // Se encriptará en el servicio
                    .rol(Usuario.Rol.valueOf(dto.getRol().toUpperCase()))
                    .build();

            Usuario usuarioCreado = usuarioService.crearUsuario(usuario);

            // 2. Crear perfil según el rol
            if (dto.getRol().equalsIgnoreCase("ESTUDIANTE")) {
                // Crear estudiante
                String codigo = estudianteService.generarCodigoEstudiante();

                Estudiante estudiante = Estudiante.builder()
                        .nombres(dto.getNombres())
                        .apellidos(dto.getApellidos())
                        .codigoEstudiante(codigo)
                        .telefono(dto.getTelefono())
                        .direccion("")
                        .distrito("")
                        .foto("")
                        .fechaNacimiento(null)
                        .usuario(usuarioCreado)
                        .build();

                Estudiante estudianteCreado = estudianteService.crearEstudiante(estudiante);

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of(
                                "success", true,
                                "message", "Estudiante registrado exitosamente",
                                "usuario", convertirAUsuarioDTO(usuarioCreado),
                                "perfil", convertirAEstudianteDTO(estudianteCreado)));

            } else if (dto.getRol().equalsIgnoreCase("DOCENTE")) {
                // Crear docente
                String codigo = docenteService.generarCodigoDocente();

                Docente docente = Docente.builder()
                        .nombres(dto.getNombres())
                        .apellidos(dto.getApellidos())
                        .codigoDocente(codigo)
                        .telefono(dto.getTelefono())
                        .direccion("")
                        .distrito("")
                        .foto("")
                        .especialidad("")
                        .fechaContratacion(null)
                        .usuario(usuarioCreado)
                        .build();

                Docente docenteCreado = docenteService.crearDocente(docente);

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of(
                                "success", true,
                                "message", "Docente registrado exitosamente",
                                "usuario", convertirAUsuarioDTO(usuarioCreado),
                                "perfil", convertirADocenteDTO(docenteCreado)));
            } else {
                // Solo crear usuario (para ADMIN u otros roles)
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of(
                                "success", true,
                                "message", "Usuario registrado exitosamente",
                                "usuario", convertirAUsuarioDTO(usuarioCreado)));
            }

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()));
        }
    }

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        List<UsuarioResponseDTO> usuariosDTO = usuarios.stream()
                .map(this::convertirAUsuarioDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuariosDTO);
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuarioPorId(@PathVariable Integer id) {
        Optional<Usuario> usuario = usuarioService.obtenerUsuarioPorId(id);
        return usuario.map(u -> ResponseEntity.ok(convertirAUsuarioDTO(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener usuarios por rol
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerUsuariosPorRol(@PathVariable String rol) {
        try {
            Usuario.Rol rolEnum = Usuario.Rol.valueOf(rol.toUpperCase());
            List<Usuario> usuarios = usuarioService.obtenerUsuariosPorRol(rolEnum);
            List<UsuarioResponseDTO> usuariosDTO = usuarios.stream()
                    .map(this::convertirAUsuarioDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(usuariosDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtener usuarios activos
    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerUsuariosActivos() {
        List<Usuario> usuarios = usuarioService.obtenerUsuariosActivos();
        List<UsuarioResponseDTO> usuariosDTO = usuarios.stream()
                .map(this::convertirAUsuarioDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuariosDTO);
    }

    // Desactivar usuario
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<UsuarioResponseDTO> desactivarUsuario(@PathVariable Integer id) {
        try {
            Usuario usuario = usuarioService.desactivarUsuario(id);
            return ResponseEntity.ok(convertirAUsuarioDTO(usuario));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Activar usuario
    @PutMapping("/{id}/activar")
    public ResponseEntity<UsuarioResponseDTO> activarUsuario(@PathVariable Integer id) {
        try {
            Usuario usuario = usuarioService.activarUsuario(id);
            return ResponseEntity.ok(convertirAUsuarioDTO(usuario));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Integer id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Métodos de conversión
    private UsuarioResponseDTO convertirAUsuarioDTO(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .email(usuario.getEmail())
                .rol(usuario.getRol().toString())
                .activo(usuario.getActivo())
                .fechaCreacion(usuario.getFechaCreacion())
                .build();
    }

    // Métodos auxiliares para convertir perfiles (simplificados)
    private EstudianteResponseDTO convertirAEstudianteDTO(
            Estudiante estudiante) {
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
                .email(estudiante.getUsuario() != null ? estudiante.getUsuario().getEmail() : null)
                .rolUsuario(estudiante.getUsuario() != null ? estudiante.getUsuario().getRol().toString() : null)
                .usuarioActivo(estudiante.getUsuario() != null ? estudiante.getUsuario().getActivo() : null)
                .build();
    }

    private DocenteResponseDTO convertirADocenteDTO(
            Docente docente) {
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