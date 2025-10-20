package edu.college.gestion_notas_backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.college.gestion_notas_backend.model.Docente;
import edu.college.gestion_notas_backend.model.Usuario;
import edu.college.gestion_notas_backend.repository.DocenteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DocenteService {
    
    private final DocenteRepository docenteRepository;
    private final UsuarioService usuarioService;
    
    // Crear docente
    public Docente crearDocente(Docente docente) {
        if (docente.getCodigoDocente() != null && 
            docenteRepository.existsByCodigoDocente(docente.getCodigoDocente())) {
            throw new RuntimeException("El código de docente ya existe: " + docente.getCodigoDocente());
        }
        
        // Validar que el usuario exista y no esté ya asignado
        if (docente.getUsuario() != null && docente.getUsuario().getIdUsuario() != null) {
            Usuario usuario = usuarioService.obtenerUsuarioPorId(docente.getUsuario().getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            if (docenteRepository.findByUsuario(usuario).isPresent()) {
                throw new RuntimeException("El usuario ya tiene un docente asignado");
            }
            
            docente.setUsuario(usuario);
        }
        
        return docenteRepository.save(docente);
    }
    
    // Obtener todos los docentes
    @Transactional(readOnly = true)
    public List<Docente> obtenerTodosLosDocentes() {
        return docenteRepository.findAll();
    }
    
    // Obtener docente por ID
    @Transactional(readOnly = true)
    public Optional<Docente> obtenerDocentePorId(Integer id) {
        return docenteRepository.findById(id);
    }
    
    // Obtener docente por usuario
    @Transactional(readOnly = true)
    public Optional<Docente> obtenerDocentePorUsuario(Usuario usuario) {
        return docenteRepository.findByUsuario(usuario);
    }
    
    // Obtener docente por ID de usuario
    @Transactional(readOnly = true)
    public Optional<Docente> obtenerDocentePorIdUsuario(Integer idUsuario) {
        return docenteRepository.findByUsuario_IdUsuario(idUsuario);
    }
    
    // Obtener docentes por especialidad
    @Transactional(readOnly = true)
    public List<Docente> obtenerDocentesPorEspecialidad(String especialidad) {
        return docenteRepository.findByEspecialidad(especialidad);
    }
    
    // Buscar docentes por nombre
    @Transactional(readOnly = true)
    public List<Docente> buscarDocentesPorNombre(String nombre) {
        return docenteRepository.findByNombresOrApellidosContaining(nombre);
    }
    
    // Obtener docentes activos
    @Transactional(readOnly = true)
    public List<Docente> obtenerDocentesActivos() {
        return docenteRepository.findActiveDocentes();
    }
    
    // Obtener docentes por distrito
    @Transactional(readOnly = true)
    public List<Docente> obtenerDocentesPorDistrito(String distrito) {
        return docenteRepository.findByDistrito(distrito);
    }
    
    // Actualizar docente
    public Docente actualizarDocente(Integer id, Docente docenteActualizado) {
        Docente docente = docenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado con ID: " + id));
        
        docente.setNombres(docenteActualizado.getNombres());
        docente.setApellidos(docenteActualizado.getApellidos());
        docente.setTelefono(docenteActualizado.getTelefono());
        docente.setDireccion(docenteActualizado.getDireccion());
        docente.setDistrito(docenteActualizado.getDistrito());
        docente.setFoto(docenteActualizado.getFoto());
        docente.setEspecialidad(docenteActualizado.getEspecialidad());
        docente.setFechaContratacion(docenteActualizado.getFechaContratacion());
        docente.setCodigoDocente(docenteActualizado.getCodigoDocente());
        
        return docenteRepository.save(docente);
    }
    
    // Actualizar perfil del docente (datos básicos)
    public Docente actualizarPerfilDocente(Integer id, String nombres, String apellidos, String telefono, String direccion, String distrito, String foto, String especialidad, LocalDate fechaContratacion, String email) {
        Docente docente = docenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado con ID: " + id));
        
        if (nombres != null) docente.setNombres(nombres);
        if (apellidos != null) docente.setApellidos(apellidos);
        if (telefono != null) docente.setTelefono(telefono);
        if (direccion != null) docente.setDireccion(direccion);
        if (distrito != null) docente.setDistrito(distrito);
        if (foto != null) docente.setFoto(foto);
        if (fechaContratacion != null) docente.setFechaContratacion(fechaContratacion);
        if (especialidad != null) docente.setEspecialidad(especialidad);
        if( email != null && docente.getUsuario() != null) {
            Usuario usuario = docente.getUsuario();
            usuario.setEmail(email);
            usuarioService.actualizarUsuario(usuario.getIdUsuario(), usuario);
        }
        
        return docenteRepository.save(docente);
    }
    
    // Eliminar docente
    public void eliminarDocente(Integer id) {
        if (!docenteRepository.existsById(id)) {
            throw new RuntimeException("Docente no encontrado con ID: " + id);
        }
        docenteRepository.deleteById(id);
    }
    
    // Obtener estadísticas por especialidad
    @Transactional(readOnly = true)
    public List<Object[]> obtenerEstadisticasPorEspecialidad() {
        return docenteRepository.countDocentesByEspecialidad();
    }
    
    // Verificar si un docente puede ser eliminado (no tiene cursos asignados)
    @Transactional(readOnly = true)
    public boolean puedeEliminarDocente(Integer id) {
        // Esta lógica se implementará cuando tengamos el servicio de cursos
        // Por ahora retornamos true
        return docenteRepository.existsById(id);
    }
    
    // Generar código de docente automático
    public String generarCodigoDocente() {
        long count = docenteRepository.count();
        return String.format("DOC%06d", count + 1);
    }
}