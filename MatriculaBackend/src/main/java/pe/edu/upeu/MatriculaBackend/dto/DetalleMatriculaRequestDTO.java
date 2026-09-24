package pe.edu.upeu.MatriculaBackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DetalleMatriculaRequestDTO {
    @NotNull(message = "El ID del curso es obligatorio")
    private Long cursoId;
}