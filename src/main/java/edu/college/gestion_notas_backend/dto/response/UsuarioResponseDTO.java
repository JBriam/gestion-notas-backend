package edu.college.gestion_notas_backend.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {
    
    private Integer idUsuario;
    private String email;
    private String rol;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}