package edu.college.gestion_notas_backend.controller;

import edu.college.gestion_notas_backend.dto.request.CrearDocenteDTO;
import edu.college.gestion_notas_backend.dto.response.DocenteResponseDTO;
import edu.college.gestion_notas_backend.model.Docente;
import edu.college.gestion_notas_backend.model.Usuario;
import edu.college.gestion_notas_backend.service.DocenteService;
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
@RequestMapping("/docentes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DocenteController {
    
    private final DocenteService docenteService;
    private final UsuarioService usuarioService;
    
    // Crear docente
    @PostMapping
    public ResponseEntity<DocenteResponseDTO> crearDocente(@Valid @RequestBody CrearDocenteDTO crearDocenteDTO) {
        try {
            Docente docente = Docente.builder()
                .nombres(crearDocenteDTO.getNombres())
                .apellidos(crearDocenteDTO.getApellidos())
                .telefono(crearDocenteDTO.getTelefono())
                .distrito(crearDocenteDTO.getDistrito())
                .foto(crearDocenteDTO.getFoto())
                .especialidad(crearDocenteDTO.getEspecialidad())
                .fechaContratacion(crearDocenteDTO.getFechaContratacion())
                .build();
            
            // Si se proporciona ID de usuario, asociarlo
            if (crearDocenteDTO.getIdUsuario() != null) {
                Optional<Usuario> usuario = usuarioService.obtenerUsuarioPorId(crearDocenteDTO.getIdUsuario());
                usuario.ifPresent(docente::setUsuario);
            }
            
            Docente docenteCreado = docenteService.crearDocente(docente);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(docenteCreado));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    
    // Obtener todos los docentes
    @GetMapping
    public ResponseEntity<List<DocenteResponseDTO>> obtenerTodosLosDocentes() {
        List<Docente> docentes = docenteService.obtenerTodosLosDocentes();
        List<DocenteResponseDTO> docentesDTO = docentes.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(docentesDTO);
    }
    
    // Obtener docentes activos
    @GetMapping("/activos")
    public ResponseEntity<List<DocenteResponseDTO>> obtenerDocentesActivos() {
        List<Docente> docentes = docenteService.obtenerDocentesActivos();
        List<DocenteResponseDTO> docentesDTO = docentes.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(docentesDTO);
    }
    
    // Obtener docente por ID
    @GetMapping("/{id}")
    public ResponseEntity<DocenteResponseDTO> obtenerDocentePorId(@PathVariable Integer id) {
        Optional<Docente> docente = docenteService.obtenerDocentePorId(id);
        return docente.map(d -> ResponseEntity.ok(convertirADTO(d)))
                     .orElse(ResponseEntity.notFound().build());
    }
    
    // Obtener docente por ID de usuario
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<DocenteResponseDTO> obtenerDocentePorIdUsuario(@PathVariable Integer idUsuario) {
        Optional<Docente> docente = docenteService.obtenerDocentePorIdUsuario(idUsuario);
        return docente.map(d -> ResponseEntity.ok(convertirADTO(d)))
                     .orElse(ResponseEntity.notFound().build());
    }
    
    // Buscar docentes por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<DocenteResponseDTO>> buscarDocentesPorNombre(@RequestParam String nombre) {
        List<Docente> docentes = docenteService.buscarDocentesPorNombre(nombre);
        List<DocenteResponseDTO> docentesDTO = docentes.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(docentesDTO);
    }
    
    // Obtener docentes por especialidad
    @GetMapping("/especialidad/{especialidad}")
    public ResponseEntity<List<DocenteResponseDTO>> obtenerDocentesPorEspecialidad(@PathVariable String especialidad) {
        List<Docente> docentes = docenteService.obtenerDocentesPorEspecialidad(especialidad);
        List<DocenteResponseDTO> docentesDTO = docentes.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(docentesDTO);
    }
    
    // Obtener docentes por distrito
    @GetMapping("/distrito/{distrito}")
    public ResponseEntity<List<DocenteResponseDTO>> obtenerDocentesPorDistrito(@PathVariable String distrito) {
        List<Docente> docentes = docenteService.obtenerDocentesPorDistrito(distrito);
        List<DocenteResponseDTO> docentesDTO = docentes.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(docentesDTO);
    }
    
    // Actualizar perfil del docente (datos específicos)
    @PutMapping("/{id}/perfil")
    public ResponseEntity<DocenteResponseDTO> actualizarPerfilDocente(
            @PathVariable Integer id, 
            @RequestBody CrearDocenteDTO perfilDTO) {
        try {
            Docente docente = docenteService.actualizarPerfilDocente(
                id, perfilDTO.getTelefono(), perfilDTO.getDistrito(), 
                perfilDTO.getFoto(), perfilDTO.getEspecialidad());
            return ResponseEntity.ok(convertirADTO(docente));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Actualizar docente completo
    @PutMapping("/{id}")
    public ResponseEntity<DocenteResponseDTO> actualizarDocente(
            @PathVariable Integer id, 
            @Valid @RequestBody CrearDocenteDTO docenteDTO) {
        try {
            Docente docenteActualizado = Docente.builder()
                .nombres(docenteDTO.getNombres())
                .apellidos(docenteDTO.getApellidos())
                .telefono(docenteDTO.getTelefono())
                .distrito(docenteDTO.getDistrito())
                .foto(docenteDTO.getFoto())
                .especialidad(docenteDTO.getEspecialidad())
                .fechaContratacion(docenteDTO.getFechaContratacion())
                .build();
            
            Docente docente = docenteService.actualizarDocente(id, docenteActualizado);
            return ResponseEntity.ok(convertirADTO(docente));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Eliminar docente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDocente(@PathVariable Integer id) {
        try {
            docenteService.eliminarDocente(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Obtener estadísticas por especialidad
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
            .distrito(docente.getDistrito())
            .foto(docente.getFoto())
            .especialidad(docente.getEspecialidad())
            .fechaContratacion(docente.getFechaContratacion())
            .username(docente.getUsuario() != null ? docente.getUsuario().getUsername() : null)
            .email(docente.getUsuario() != null ? docente.getUsuario().getEmail() : null)
            .rolUsuario(docente.getUsuario() != null ? docente.getUsuario().getRol().toString() : null)
            .usuarioActivo(docente.getUsuario() != null ? docente.getUsuario().getActivo() : null)
            .build();
    }
}