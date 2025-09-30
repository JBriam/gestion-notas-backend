package edu.college.gestion_notas_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocenteResponseDTO {
    
    private Integer idDocente;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String distrito;
    private String foto;
    private String especialidad;
    private LocalDate fechaContratacion;
    
    // Información básica del usuario asociado
    private String username;
    private String email;
    private String rolUsuario;
    private Boolean usuarioActivo;
}