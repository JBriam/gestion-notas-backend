package edu.college.gestion_notas_backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.college.gestion_notas_backend.model.Estudiante;
import edu.college.gestion_notas_backend.model.Usuario;
import edu.college.gestion_notas_backend.repository.EstudianteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EstudianteService {
    
    private final EstudianteRepository estudianteRepository;
    private final UsuarioService usuarioService;
    private final FileStorageService fileStorageService;
    
    // Crear estudiante
    public Estudiante crearEstudiante(Estudiante estudiante) {
        if (estudiante.getCodigoEstudiante() != null && 
            estudianteRepository.existsByCodigoEstudiante(estudiante.getCodigoEstudiante())) {
            throw new RuntimeException("El código de estudiante ya existe: " + estudiante.getCodigoEstudiante());
        }

        // Validar que el usuario exista y no esté ya asignado
        if (estudiante.getUsuario() != null && estudiante.getUsuario().getIdUsuario() != null) {
            Usuario usuario = usuarioService.obtenerUsuarioPorId(estudiante.getUsuario().getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            if (estudianteRepository.findByUsuario(usuario).isPresent()) {
                throw new RuntimeException("El usuario ya tiene un estudiante asignado");
            }
            
            estudiante.setUsuario(usuario);
        }
        
        return estudianteRepository.save(estudiante);
    }
    
    // Obtener todos los estudiantes
    @Transactional(readOnly = true)
    public List<Estudiante> obtenerTodosLosEstudiantes() {
        return estudianteRepository.findAll();
    }
    
    // Obtener estudiante por ID
    @Transactional(readOnly = true)
    public Optional<Estudiante> obtenerEstudiantePorId(Integer id) {
        return estudianteRepository.findById(id);
    }
    
    // Obtener estudiante por código
    @Transactional(readOnly = true)
    public Optional<Estudiante> obtenerEstudiantePorCodigo(String codigo) {
        return estudianteRepository.findByCodigoEstudiante(codigo);
    }
    
    // Obtener estudiante por usuario
    @Transactional(readOnly = true)
    public Optional<Estudiante> obtenerEstudiantePorUsuario(Usuario usuario) {
        return estudianteRepository.findByUsuario(usuario);
    }
    
    // Obtener estudiante por ID de usuario
    @Transactional(readOnly = true)
    public Optional<Estudiante> obtenerEstudiantePorIdUsuario(Integer idUsuario) {
        return estudianteRepository.findByUsuario_IdUsuario(idUsuario);
    }
    
    // Buscar estudiantes por nombre
    @Transactional(readOnly = true)
    public List<Estudiante> buscarEstudiantesPorNombre(String nombre) {
        return estudianteRepository.findByNombresOrApellidosContaining(nombre);
    }
    
    // Obtener estudiantes por distrito
    @Transactional(readOnly = true)
    public List<Estudiante> obtenerEstudiantesPorDistrito(String distrito) {
        return estudianteRepository.findByDistrito(distrito);
    }
    
    // Actualizar estudiante
    public Estudiante actualizarEstudiante(Integer id, Estudiante estudianteActualizado) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con ID: " + id));
        
        // Verificar si el nuevo código ya existe (excluyendo el estudiante actual)
        if (estudianteActualizado.getCodigoEstudiante() != null &&
            !estudianteActualizado.getCodigoEstudiante().equals(estudiante.getCodigoEstudiante()) &&
            estudianteRepository.existsByCodigoEstudiante(estudianteActualizado.getCodigoEstudiante())) {
            throw new RuntimeException("El código de estudiante ya existe: " + estudianteActualizado.getCodigoEstudiante());
        }
        
        estudiante.setNombres(estudianteActualizado.getNombres());
        estudiante.setApellidos(estudianteActualizado.getApellidos());
        estudiante.setTelefono(estudianteActualizado.getTelefono());
        estudiante.setDireccion(estudianteActualizado.getDireccion());
        estudiante.setDistrito(estudianteActualizado.getDistrito());
        estudiante.setFoto(estudianteActualizado.getFoto());
        estudiante.setFechaNacimiento(estudianteActualizado.getFechaNacimiento());
        estudiante.setCodigoEstudiante(estudianteActualizado.getCodigoEstudiante());
        return estudianteRepository.save(estudiante);
    }
    
    // Actualizar perfil del estudiante (datos básicos)
    public Estudiante actualizarPerfilEstudiante(Integer id, String nombres, String apellidos, String telefono, String direccion, String distrito, String foto, LocalDate fechaNacimiento, String email) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con ID: " + id));

        if (nombres != null) estudiante.setNombres(nombres);
        if (apellidos != null) estudiante.setApellidos(apellidos);
        if (telefono != null) estudiante.setTelefono(telefono);
        if (direccion != null) estudiante.setDireccion(direccion);
        if (distrito != null) estudiante.setDistrito(distrito);
        if (foto != null) estudiante.setFoto(foto);
        if (fechaNacimiento != null) estudiante.setFechaNacimiento(fechaNacimiento);
        if( email != null && estudiante.getUsuario() != null) {
            Usuario usuario = estudiante.getUsuario();
            usuario.setEmail(email);
            usuarioService.actualizarUsuario(usuario.getIdUsuario(), usuario);
        }
        
        return estudianteRepository.save(estudiante);
    }

    // Actualizar foto del estudiante
    public Estudiante actualizarFoto(Integer id, org.springframework.web.multipart.MultipartFile foto) {
        Estudiante estudiante = estudianteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado con ID: " + id));
        
        try {
            // Eliminar foto anterior si existe
            if (estudiante.getFoto() != null && !estudiante.getFoto().isEmpty()) {
                fileStorageService.deleteFile(estudiante.getFoto(), "estudiantes");
            }
            
            // Guardar el archivo
            String nombreArchivo = fileStorageService.storeFile(foto, "estudiantes");
            
            // Actualizar la ruta de la foto en el estudiante
            estudiante.setFoto(nombreArchivo);
            
            return estudianteRepository.save(estudiante);
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la foto: " + e.getMessage(), e);
        }
    }
    
    // Eliminar estudiante
    public void eliminarEstudiante(Integer id) {
        if (!estudianteRepository.existsById(id)) {
            throw new RuntimeException("Estudiante no encontrado con ID: " + id);
        }
        estudianteRepository.deleteById(id);
    }
    
    // Obtener estadísticas por distrito
    @Transactional(readOnly = true)
    public List<Object[]> obtenerEstadisticasPorDistrito() {
        return estudianteRepository.countStudentsByDistrito();
    }
    
    // Generar código de estudiante automático
    public String generarCodigoEstudiante() {
        long count = estudianteRepository.count();
        return String.format("EST%06d", count + 1);
    }
}