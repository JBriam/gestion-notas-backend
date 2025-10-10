package edu.college.gestion_notas_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.college.gestion_notas_backend.model.Usuario;
import edu.college.gestion_notas_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    
    // Crear usuario
    public Usuario crearUsuario(Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new RuntimeException("El username ya existe: " + usuario.getUsername());
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya existe: " + usuario.getEmail());
        }
        
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }
    
    // Obtener todos los usuarios
    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }
    
    // Obtener usuario por ID
    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerUsuarioPorId(Integer id) {
        return usuarioRepository.findById(id);
    }
    
    // Obtener usuario por username
    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }
    
    // Obtener usuario por email
    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    
    // Obtener usuarios por rol
    @Transactional(readOnly = true)
    public List<Usuario> obtenerUsuariosPorRol(Usuario.Rol rol) {
        return usuarioRepository.findByRol(rol);
    }
    
    // Obtener usuarios activos
    @Transactional(readOnly = true)
    public List<Usuario> obtenerUsuariosActivos() {
        return usuarioRepository.findByActivoTrue();
    }
    
    // Obtener usuarios activos por rol
    @Transactional(readOnly = true)
    public List<Usuario> obtenerUsuariosActivosPorRol(Usuario.Rol rol) {
        return usuarioRepository.findActiveUsersByRole(rol);
    }
    
    // Actualizar usuario
    public Usuario actualizarUsuario(Integer id, Usuario usuarioActualizado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        // Verificar si el nuevo username ya existe (excluyendo el usuario actual)
        if (!usuario.getUsername().equals(usuarioActualizado.getUsername()) &&
            usuarioRepository.existsByUsername(usuarioActualizado.getUsername())) {
            throw new RuntimeException("El username ya existe: " + usuarioActualizado.getUsername());
        }
        
        // Verificar si el nuevo email ya existe (excluyendo el usuario actual)
        if (!usuario.getEmail().equals(usuarioActualizado.getEmail()) &&
            usuarioRepository.existsByEmail(usuarioActualizado.getEmail())) {
            throw new RuntimeException("El email ya existe: " + usuarioActualizado.getEmail());
        }
        
        usuario.setUsername(usuarioActualizado.getUsername());
        usuario.setEmail(usuarioActualizado.getEmail());
        usuario.setPassword(usuarioActualizado.getPassword());
        usuario.setRol(usuarioActualizado.getRol());
        usuario.setActivo(usuarioActualizado.getActivo());
        
        return usuarioRepository.save(usuario);
    }
    
    // Desactivar usuario (soft delete)
    public Usuario desactivarUsuario(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        usuario.setActivo(false);
        return usuarioRepository.save(usuario);
    }
    
    // Activar usuario
    public Usuario activarUsuario(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }
    
    // Eliminar usuario (hard delete)
    public void eliminarUsuario(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
    
    // Verificar credenciales (para login básico)
    @Transactional(readOnly = true)
    public boolean verificarCredenciales(String username, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        return usuario.isPresent() && usuario.get().getPassword().equals(password) && usuario.get().getActivo();
    }
}