package pe.edu.upeu.MatriculaBackend.dto;

import lombok.Data;

@Data
public class CarreraResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Boolean estado;
}