package pe.edu.upeu.MatriculaBackend.dto;

import lombok.Data;

@Data
public class EstudianteResponseDTO {
    private Long id;
    private String codigo;
    private String dni;
    private String nombres;
    private String apellidos;
    private String email;
    private Boolean estado;
    private Long carreraId;
    private String carreraNombre;
}