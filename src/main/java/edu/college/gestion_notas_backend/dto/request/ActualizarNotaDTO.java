package edu.college.gestion_notas_backend.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarNotaDTO {
        
    @NotNull(message = "La nota es obligatoria")
    @DecimalMin(value = "0.00", message = "La nota debe ser mayor o igual a 0.00")
    @DecimalMax(value = "20.00", message = "La nota debe ser menor o igual a 20.00")
    private BigDecimal nota;
    
    @NotNull(message = "El tipo de evaluación es obligatorio")
    private String tipoEvaluacion; // "PARCIAL", "FINAL", "TAREA", "PRACTICA", "EXAMEN"
    
    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;
}