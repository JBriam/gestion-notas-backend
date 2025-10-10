package edu.college.gestion_notas_backend.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.college.gestion_notas_backend.model.Curso;
import edu.college.gestion_notas_backend.model.Estudiante;
import edu.college.gestion_notas_backend.model.Nota;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Integer> {
    
    // Buscar notas por estudiante
    List<Nota> findByEstudiante(Estudiante estudiante);
    
    // Buscar notas por estudiante ID
    List<Nota> findByEstudiante_IdEstudiante(Integer idEstudiante);
    
    // Buscar notas por curso
    List<Nota> findByCurso(Curso curso);
    
    // Buscar notas por curso ID
    List<Nota> findByCurso_IdCurso(Integer idCurso);
    
    // Buscar notas por estudiante y curso
    List<Nota> findByEstudianteAndCurso(Estudiante estudiante, Curso curso);
    
    // Buscar por tipo de evaluación
    List<Nota> findByTipoEvaluacion(Nota.TipoEvaluacion tipoEvaluacion);
    
    // Buscar notas por estudiante y tipo de evaluación
    List<Nota> findByEstudianteAndTipoEvaluacion(Estudiante estudiante, Nota.TipoEvaluacion tipoEvaluacion);
    
    // Promedio de notas por estudiante
    @Query("SELECT AVG(n.nota) FROM Nota n WHERE n.estudiante.idEstudiante = :idEstudiante")
    Double findAverageGradeByStudent(@Param("idEstudiante") Integer idEstudiante);
    
    // Promedio de notas por curso
    @Query("SELECT AVG(n.nota) FROM Nota n WHERE n.curso.idCurso = :idCurso")
    Double findAverageGradeByCourse(@Param("idCurso") Integer idCurso);
    
    // Promedio por estudiante y curso
    @Query("SELECT AVG(n.nota) FROM Nota n WHERE n.estudiante.idEstudiante = :idEstudiante " +
           "AND n.curso.idCurso = :idCurso")
    Double findAverageGradeByStudentAndCourse(@Param("idEstudiante") Integer idEstudiante, 
                                              @Param("idCurso") Integer idCurso);
    
    // Notas mayores o iguales a un valor
    @Query("SELECT n FROM Nota n WHERE n.nota >= :minNota")
    List<Nota> findNotasWithMinGrade(@Param("minNota") BigDecimal minNota);
    
    // Mejores notas por curso (top N)
    @Query("SELECT n FROM Nota n WHERE n.curso.idCurso = :idCurso ORDER BY n.nota DESC")
    List<Nota> findTopGradesByCourse(@Param("idCurso") Integer idCurso);
    
    // Contar notas por tipo de evaluación
    @Query("SELECT n.tipoEvaluacion, COUNT(n) FROM Nota n GROUP BY n.tipoEvaluacion")
    List<Object[]> countNotasByTipoEvaluacion();
}