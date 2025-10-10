package edu.college.gestion_notas_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotaResponseDTO {
    
    private Integer idNota;
    private BigDecimal nota;
    private String tipoEvaluacion;
    private LocalDateTime fechaRegistro;
    private String observaciones;
    
    // Información del estudiante
    private Integer idEstudiante;
    private String nombreEstudiante;
    private String apellidosEstudiante;
    private String codigoEstudiante;
    
    // Información del curso
    private Integer idCurso;
    private String nombreCurso;
    private String codigoCurso;
    
    // Estado académico calculado
    private String estadoAcademico; // "EXCELENTE", "BUENO", "REGULAR", "DESAPROBADO"
}