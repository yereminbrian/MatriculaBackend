package pe.edu.upeu.MatriculaBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarreraResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Boolean estado;
}