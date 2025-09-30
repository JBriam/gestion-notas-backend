package edu.college.gestion_notas_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.college.gestion_notas_backend.model.Curso;
import edu.college.gestion_notas_backend.model.Docente;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Integer> {
    
    // Buscar por código de curso
    Optional<Curso> findByCodigoCurso(String codigoCurso);
    
    // Buscar por nombre (like)
    List<Curso> findByNombreContainingIgnoreCase(String nombre);
    
    // Buscar cursos activos
    List<Curso> findByActivoTrue();
    
    // Buscar por docente
    List<Curso> findByDocente(Docente docente);
    
    // Buscar por docente ID
    List<Curso> findByDocente_IdDocente(Integer idDocente);
    
    // Buscar cursos activos por docente
    @Query("SELECT c FROM Curso c WHERE c.docente.idDocente = :idDocente AND c.activo = true")
    List<Curso> findActiveCursosByDocente(@Param("idDocente") Integer idDocente);
    
    // Buscar por créditos
    List<Curso> findByCreditos(Integer creditos);
    
    // Verificar si existe código de curso
    boolean existsByCodigoCurso(String codigoCurso);
    
    // Contar cursos por docente
    @Query("SELECT c.docente.nombres, c.docente.apellidos, COUNT(c) FROM Curso c " +
           "WHERE c.activo = true GROUP BY c.docente.idDocente, c.docente.nombres, c.docente.apellidos")
    List<Object[]> countCursosByDocente();
    
    // Buscar cursos con más de X créditos
    @Query("SELECT c FROM Curso c WHERE c.creditos >= :minCreditos AND c.activo = true")
    List<Curso> findCursosWithMinCredits(@Param("minCreditos") Integer minCreditos);
}