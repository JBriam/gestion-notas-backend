package edu.college.gestion_notas_backend.dto.request;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarPerfilEstudianteDTO {

    @Size(max = 50, message = "Los nombres no pueden exceder 50 caracteres")
    private String nombres;

    @Size(max = 50, message = "Los apellidos no pueden exceder 50 caracteres")
    private String apellidos;
    
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;
    
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String direccion;
    
    @Size(max = 100, message = "El distrito no puede exceder 100 caracteres")
    private String distrito;
    
    private MultipartFile foto;

    private LocalDate fechaNacimiento;

    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;
}