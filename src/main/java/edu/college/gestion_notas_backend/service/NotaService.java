package edu.college.gestion_notas_backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.college.gestion_notas_backend.model.Curso;
import edu.college.gestion_notas_backend.model.Estudiante;
import edu.college.gestion_notas_backend.model.Nota;
import edu.college.gestion_notas_backend.repository.NotaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NotaService {
    
    private final NotaRepository notaRepository;
    
    // Crear nota
    public Nota crearNota(Nota nota) {
        // Validar que la nota esté en rango válido (0-20 para sistema peruano)
        if (nota.getNota().compareTo(BigDecimal.ZERO) < 0 || 
            nota.getNota().compareTo(new BigDecimal("20.00")) > 0) {
            throw new RuntimeException("La nota debe estar entre 0.00 y 20.00");
        }
        
        // Establecer valores por defecto
        if (nota.getTipoEvaluacion() == null) {
            nota.setTipoEvaluacion(Nota.TipoEvaluacion.PARCIAL);
        }
        nota.setFechaRegistro(LocalDateTime.now());
        
        return notaRepository.save(nota);
    }
    
    // Obtener todas las notas
    @Transactional(readOnly = true)
    public List<Nota> obtenerTodasLasNotas() {
        return notaRepository.findAll();
    }
    
    // Obtener nota por ID
    @Transactional(readOnly = true)
    public Optional<Nota> obtenerNotaPorId(Integer id) {
        return notaRepository.findById(id);
    }
    
    // Obtener notas por estudiante
    @Transactional(readOnly = true)
    public List<Nota> obtenerNotasPorEstudiante(Estudiante estudiante) {
        return notaRepository.findByEstudiante(estudiante);
    }
    
    // Obtener notas por ID de estudiante
    @Transactional(readOnly = true)
    public List<Nota> obtenerNotasPorIdEstudiante(Integer idEstudiante) {
        return notaRepository.findByEstudiante_IdEstudiante(idEstudiante);
    }
    
    // Obtener notas por curso
    @Transactional(readOnly = true)
    public List<Nota> obtenerNotasPorCurso(Curso curso) {
        return notaRepository.findByCurso(curso);
    }
    
    // Obtener notas por ID de curso
    @Transactional(readOnly = true)
    public List<Nota> obtenerNotasPorIdCurso(Integer idCurso) {
        return notaRepository.findByCurso_IdCurso(idCurso);
    }
    
    // Obtener notas por estudiante y curso
    @Transactional(readOnly = true)
    public List<Nota> obtenerNotasPorEstudianteYCurso(Estudiante estudiante, Curso curso) {
        return notaRepository.findByEstudianteAndCurso(estudiante, curso);
    }
    
    // Obtener notas por tipo de evaluación
    @Transactional(readOnly = true)
    public List<Nota> obtenerNotasPorTipoEvaluacion(Nota.TipoEvaluacion tipoEvaluacion) {
        return notaRepository.findByTipoEvaluacion(tipoEvaluacion);
    }
    
    // Obtener notas por estudiante y tipo de evaluación
    @Transactional(readOnly = true)
    public List<Nota> obtenerNotasPorEstudianteYTipo(Estudiante estudiante, Nota.TipoEvaluacion tipoEvaluacion) {
        return notaRepository.findByEstudianteAndTipoEvaluacion(estudiante, tipoEvaluacion);
    }
    
    // Calcular promedio por estudiante
    @Transactional(readOnly = true)
    public Double calcularPromedioPorEstudiante(Integer idEstudiante) {
        Double promedio = notaRepository.findAverageGradeByStudent(idEstudiante);
        return promedio != null ? promedio : 0.0;
    }
    
    // Calcular promedio por curso
    @Transactional(readOnly = true)
    public Double calcularPromedioPorCurso(Integer idCurso) {
        Double promedio = notaRepository.findAverageGradeByCourse(idCurso);
        return promedio != null ? promedio : 0.0;
    }
    
    // Calcular promedio por estudiante y curso
    @Transactional(readOnly = true)
    public Double calcularPromedioPorEstudianteYCurso(Integer idEstudiante, Integer idCurso) {
        Double promedio = notaRepository.findAverageGradeByStudentAndCourse(idEstudiante, idCurso);
        return promedio != null ? promedio : 0.0;
    }
    
    // Obtener notas aprobatorias (>= 11)
    @Transactional(readOnly = true)
    public List<Nota> obtenerNotasAprobatorias() {
        return notaRepository.findNotasWithMinGrade(new BigDecimal("11.00"));
    }
    
    // Obtener notas con calificación mínima
    @Transactional(readOnly = true)
    public List<Nota> obtenerNotasConMinimo(BigDecimal notaMinima) {
        return notaRepository.findNotasWithMinGrade(notaMinima);
    }
    
    // Obtener mejores notas por curso
    @Transactional(readOnly = true)
    public List<Nota> obtenerMejoresNotasPorCurso(Integer idCurso, int limite) {
        List<Nota> notas = notaRepository.findTopGradesByCourse(idCurso);
        return notas.size() > limite ? notas.subList(0, limite) : notas;
    }
    
    // Actualizar nota
    public Nota actualizarNota(Integer id, Nota notaActualizada) {
        Nota nota = notaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nota no encontrada con ID: " + id));
        
        // Validar que la nota esté en rango válido
        if (notaActualizada.getNota().compareTo(BigDecimal.ZERO) < 0 || 
            notaActualizada.getNota().compareTo(new BigDecimal("20.00")) > 0) {
            throw new RuntimeException("La nota debe estar entre 0.00 y 20.00");
        }
        
        nota.setNota(notaActualizada.getNota());
        nota.setTipoEvaluacion(notaActualizada.getTipoEvaluacion());
        nota.setObservaciones(notaActualizada.getObservaciones());
        // No actualizamos fechaRegistro para mantener el historial
        
        return notaRepository.save(nota);
    }
    
    // Eliminar nota
    public void eliminarNota(Integer id) {
        if (!notaRepository.existsById(id)) {
            throw new RuntimeException("Nota no encontrada con ID: " + id);
        }
        notaRepository.deleteById(id);
    }
    
    // Obtener estadísticas por tipo de evaluación
    @Transactional(readOnly = true)
    public List<Object[]> obtenerEstadisticasPorTipoEvaluacion() {
        return notaRepository.countNotasByTipoEvaluacion();
    }
    
    // Verificar si un estudiante aprobó un curso (promedio >= 11)
    @Transactional(readOnly = true)
    public boolean estudianteAproboCurso(Integer idEstudiante, Integer idCurso) {
        Double promedio = calcularPromedioPorEstudianteYCurso(idEstudiante, idCurso);
        return promedio >= 11.0;
    }
    
    // Obtener estado académico del estudiante en un curso
    @Transactional(readOnly = true)
    public String obtenerEstadoAcademico(Integer idEstudiante, Integer idCurso) {
        Double promedio = calcularPromedioPorEstudianteYCurso(idEstudiante, idCurso);
        
        if (promedio >= 18.0) return "EXCELENTE";
        else if (promedio >= 16.0) return "MUY BUENO";
        else if (promedio >= 14.0) return "BUENO";
        else if (promedio >= 11.0) return "REGULAR";
        else return "DESAPROBADO";
    }
}