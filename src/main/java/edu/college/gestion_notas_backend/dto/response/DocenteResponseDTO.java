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
public class DocenteResponseDTO {
    
    private Integer idDocente;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String direccion;
    private String distrito;
    private String foto;
    private String especialidad;
    private LocalDate fechaContratacion;
    private String codigoDocente;
    
    // Información básica del usuario asociado
    private String email;
    private String rolUsuario;
    private Boolean usuarioActivo;
}