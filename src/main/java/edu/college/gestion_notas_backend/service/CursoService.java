package edu.college.gestion_notas_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.college.gestion_notas_backend.model.Curso;
import edu.college.gestion_notas_backend.model.Docente;
import edu.college.gestion_notas_backend.repository.CursoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CursoService {
    
    private final CursoRepository cursoRepository;
    
    // Crear curso
    public Curso crearCurso(Curso curso) {
        if (curso.getCodigoCurso() != null && 
            cursoRepository.existsByCodigoCurso(curso.getCodigoCurso())) {
            throw new RuntimeException("El código de curso ya existe: " + curso.getCodigoCurso());
        }
        
        // Establecer valores por defecto
        if (curso.getCreditos() == null) {
            curso.setCreditos(3);
        }
        if (curso.getActivo() == null) {
            curso.setActivo(true);
        }
        
        return cursoRepository.save(curso);
    }
    
    // Obtener todos los cursos
    @Transactional(readOnly = true)
    public List<Curso> obtenerTodosLosCursos() {
        return cursoRepository.findAll();
    }
    
    // Obtener curso por ID
    @Transactional(readOnly = true)
    public Optional<Curso> obtenerCursoPorId(Integer id) {
        return cursoRepository.findById(id);
    }
    
    // Obtener curso por código
    @Transactional(readOnly = true)
    public Optional<Curso> obtenerCursoPorCodigo(String codigo) {
        return cursoRepository.findByCodigoCurso(codigo);
    }
    
    // Obtener cursos activos
    @Transactional(readOnly = true)
    public List<Curso> obtenerCursosActivos() {
        return cursoRepository.findByActivoTrue();
    }
    
    // Buscar cursos por nombre
    @Transactional(readOnly = true)
    public List<Curso> buscarCursosPorNombre(String nombre) {
        return cursoRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    // Obtener cursos por docente
    @Transactional(readOnly = true)
    public List<Curso> obtenerCursosPorDocente(Docente docente) {
        return cursoRepository.findByDocente(docente);
    }
    
    // Obtener cursos por ID de docente
    @Transactional(readOnly = true)
    public List<Curso> obtenerCursosPorIdDocente(Integer idDocente) {
        return cursoRepository.findByDocente_IdDocente(idDocente);
    }
    
    // Obtener cursos activos por docente
    @Transactional(readOnly = true)
    public List<Curso> obtenerCursosActivosPorDocente(Integer idDocente) {
        return cursoRepository.findActiveCursosByDocente(idDocente);
    }
    
    // Obtener cursos por créditos
    @Transactional(readOnly = true)
    public List<Curso> obtenerCursosPorCreditos(Integer creditos) {
        return cursoRepository.findByCreditos(creditos);
    }
    
    // Obtener cursos con mínimo de créditos
    @Transactional(readOnly = true)
    public List<Curso> obtenerCursosConMinCreditos(Integer minCreditos) {
        return cursoRepository.findCursosWithMinCredits(minCreditos);
    }
    
    // Actualizar curso
    public Curso actualizarCurso(Integer id, Curso cursoActualizado) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con ID: " + id));
        
        // Verificar si el nuevo código ya existe (excluyendo el curso actual)
        if (cursoActualizado.getCodigoCurso() != null &&
            !cursoActualizado.getCodigoCurso().equals(curso.getCodigoCurso()) &&
            cursoRepository.existsByCodigoCurso(cursoActualizado.getCodigoCurso())) {
            throw new RuntimeException("El código de curso ya existe: " + cursoActualizado.getCodigoCurso());
        }
        
        curso.setNombre(cursoActualizado.getNombre());
        curso.setCodigoCurso(cursoActualizado.getCodigoCurso());
        curso.setDescripcion(cursoActualizado.getDescripcion());
        curso.setCreditos(cursoActualizado.getCreditos());
        curso.setActivo(cursoActualizado.getActivo());
        curso.setDocente(cursoActualizado.getDocente());
        
        return cursoRepository.save(curso);
    }
    
    // Asignar docente a curso
    public Curso asignarDocente(Integer idCurso, Docente docente) {
        Curso curso = cursoRepository.findById(idCurso)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con ID: " + idCurso));
        
        curso.setDocente(docente);
        return cursoRepository.save(curso);
    }
    
    // Desactivar curso (soft delete)
    public Curso desactivarCurso(Integer id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con ID: " + id));
        
        curso.setActivo(false);
        return cursoRepository.save(curso);
    }
    
    // Activar curso
    public Curso activarCurso(Integer id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con ID: " + id));
        
        curso.setActivo(true);
        return cursoRepository.save(curso);
    }
    
    // Eliminar curso (hard delete)
    public void eliminarCurso(Integer id) {
        if (!cursoRepository.existsById(id)) {
            throw new RuntimeException("Curso no encontrado con ID: " + id);
        }
        cursoRepository.deleteById(id);
    }
    
    // Obtener estadísticas por docente
    @Transactional(readOnly = true)
    public List<Object[]> obtenerEstadisticasPorDocente() {
        return cursoRepository.countCursosByDocente();
    }
    
    // Generar código de curso automático
    public String generarCodigoCurso() {
        long count = cursoRepository.count();
        return String.format("CUR%04d", count + 1);
    }
}