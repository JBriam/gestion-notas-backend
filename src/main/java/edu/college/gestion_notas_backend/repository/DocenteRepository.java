package edu.college.gestion_notas_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.college.gestion_notas_backend.model.Docente;
import edu.college.gestion_notas_backend.model.Usuario;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, Integer> {
    
    // Buscar por usuario
    Optional<Docente> findByUsuario(Usuario usuario);
    
    // Buscar por usuario ID
    Optional<Docente> findByUsuario_IdUsuario(Integer idUsuario);
    
    // Buscar por especialidad
    List<Docente> findByEspecialidad(String especialidad);
    
    // Buscar por nombres y apellidos (like)
    @Query("SELECT d FROM Docente d WHERE " +
           "LOWER(d.nombres) LIKE LOWER(CONCAT('%', :nombre, '%')) OR " +
           "LOWER(d.apellidos) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Docente> findByNombresOrApellidosContaining(@Param("nombre") String nombre);
    
    // Buscar docentes activos (usuario activo)
    @Query("SELECT d FROM Docente d WHERE d.usuario.activo = true")
    List<Docente> findActiveDocentes();
    
    // Buscar por distrito
    List<Docente> findByDistrito(String distrito);
    
    // Contar docentes por especialidad
    @Query("SELECT d.especialidad, COUNT(d) FROM Docente d GROUP BY d.especialidad")
    List<Object[]> countDocentesByEspecialidad();
}