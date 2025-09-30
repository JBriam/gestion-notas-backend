package edu.college.gestion_notas_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearCursoDTO {
    
    @NotBlank(message = "El nombre del curso es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
    
    @Size(max = 20, message = "El código no puede exceder 20 caracteres")
    private String codigoCurso;
    
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;
    
    @NotNull(message = "Los créditos son obligatorios")
    @Positive(message = "Los créditos deben ser un número positivo")
    private Integer creditos;
    
    // ID del docente responsable
    private Integer idDocente;
}