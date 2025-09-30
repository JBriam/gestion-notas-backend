package edu.college.gestion_notas_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursoResponseDTO {
    
    private Integer idCurso;
    private String nombre;
    private String codigoCurso;
    private String descripcion;
    private Integer creditos;
    private Boolean activo;
    
    // Información del docente responsable
    private Integer idDocente;
    private String nombreDocente;
    private String apellidosDocente;
    private String especialidadDocente;
}