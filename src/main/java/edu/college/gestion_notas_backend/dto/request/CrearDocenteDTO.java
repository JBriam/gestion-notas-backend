package edu.college.gestion_notas_backend.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearDocenteDTO {
    
    @NotNull(message = "El ID del usuario es obligatorio")
    private Integer idUsuario;
    
    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Los nombres no pueden exceder 100 caracteres")
    private String nombres;
    
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos no pueden exceder 100 caracteres")
    private String apellidos;
    
    @Size(max = 20, message = "El código de docente no puede exceder 20 caracteres")
    private String codigoDocente; // Opcional, se puede generar automáticamente
    
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;
    
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String direccion;
    
    @Size(max = 100, message = "El distrito no puede exceder 100 caracteres")
    private String distrito;
    
    @Size(max = 100000, message = "La foto no puede exceder 100KB")
    private String foto;
    
    @Size(max = 100, message = "La especialidad no puede exceder 100 caracteres")
    private String especialidad;
    
    private LocalDate fechaContratacion;
}