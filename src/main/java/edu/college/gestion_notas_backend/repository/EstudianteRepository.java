package edu.college.gestion_notas_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.college.gestion_notas_backend.model.Estudiante;
import edu.college.gestion_notas_backend.model.Usuario;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Integer> {
    
    // Buscar por código de estudiante
    Optional<Estudiante> findByCodigoEstudiante(String codigoEstudiante);
    
    // Buscar por email
    Optional<Estudiante> findByEmail(String email);
    
    // Buscar por usuario
    Optional<Estudiante> findByUsuario(Usuario usuario);
    
    // Buscar por usuario ID
    Optional<Estudiante> findByUsuario_IdUsuario(Integer idUsuario);
    
    // Buscar por nombres y apellidos (like)
    @Query("SELECT e FROM Estudiante e WHERE " +
           "LOWER(e.nombres) LIKE LOWER(CONCAT('%', :nombre, '%')) OR " +
           "LOWER(e.apellidos) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Estudiante> findByNombresOrApellidosContaining(@Param("nombre") String nombre);
    
    // Buscar por distrito
    List<Estudiante> findByDistrito(String distrito);
    
    // Verificar si existe código de estudiante
    boolean existsByCodigoEstudiante(String codigoEstudiante);
    
    // Contar estudiantes por distrito
    @Query("SELECT e.distrito, COUNT(e) FROM Estudiante e GROUP BY e.distrito")
    List<Object[]> countStudentsByDistrito();
}