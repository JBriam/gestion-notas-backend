package edu.college.gestion_notas_backend.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudianteResponseDTO {
    
    private Integer idEstudiante;
    private String nombres;
    private String apellidos;
    private String email;
    private String telefono;
    private String distrito;
    private String foto;
    private LocalDate fechaNacimiento;
    private String codigoEstudiante;
    
    // Información básica del usuario asociado
    private String username;
    private String rolUsuario;
    private Boolean usuarioActivo;
}