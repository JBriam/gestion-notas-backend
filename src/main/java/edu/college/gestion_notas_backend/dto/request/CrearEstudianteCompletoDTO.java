package edu.college.gestion_notas_backend.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearEstudianteCompletoDTO {

    // Datos del usuario    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    
    // Datos del estudiante
    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Los nombres no pueden exceder 100 caracteres")
    private String nombres;
    
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos no pueden exceder 100 caracteres")
    private String apellidos;
    
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;

    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String direccion;
    
    @Size(max = 100, message = "El distrito no puede exceder 100 caracteres")
    private String distrito;
    
    @Size(max = 255, message = "La URL de la foto no puede exceder 255 caracteres")
    private String foto;
    
    private LocalDate fechaNacimiento;
    
    @Size(max = 20, message = "El código de estudiante no puede exceder 20 caracteres")
    private String codigoEstudiante;
}