package edu.college.gestion_notas_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    @NotBlank(message = "El email es obligatorio")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}