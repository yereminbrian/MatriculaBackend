package pe.edu.upeu.MatriculaBackend.dto;

import lombok.Data;

@Data
public class CursoResponseDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private Integer creditos;
    private Integer ciclo;
    private Integer vacantes;
    private Boolean estado;
    private Long carreraId;
    private String carreraNombre;
}