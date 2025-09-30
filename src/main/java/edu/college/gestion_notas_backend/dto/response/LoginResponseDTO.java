package edu.college.gestion_notas_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    
    private String message;
    private Boolean success;
    private UsuarioResponseDTO usuario;
    
    // Información adicional del perfil según el rol
    private EstudianteResponseDTO perfilEstudiante;
    private DocenteResponseDTO perfilDocente;
}