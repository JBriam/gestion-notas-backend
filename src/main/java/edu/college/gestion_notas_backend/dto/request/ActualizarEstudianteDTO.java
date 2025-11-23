package edu.college.gestion_notas_backend.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarEstudianteDTO {
    
    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Los nombres no pueden exceder 100 caracteres")
    private String nombres;
    
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos no pueden exceder 100 caracteres")
    private String apellidos;
    
    @Size(max = 20, message = "El código de estudiante no puede exceder 20 caracteres")
    private String codigoEstudiante;
    
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;
    
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String direccion;
    
    @Size(max = 100, message = "El distrito no puede exceder 100 caracteres")
    private String distrito;
    
    @Size(max = 255, message = "La URL de la foto no puede exceder 255 caracteres")
    private String foto;
    
    private LocalDate fechaNacimiento;
    
    // NO incluir idUsuario - no se debe cambiar al actualizar
}