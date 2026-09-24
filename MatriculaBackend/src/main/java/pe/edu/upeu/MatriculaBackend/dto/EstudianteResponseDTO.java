package pe.edu.upeu.MatriculaBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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