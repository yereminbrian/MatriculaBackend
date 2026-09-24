package pe.edu.upeu.MatriculaBackend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DetalleMatriculaResponseDTO {
    private Long id;
    private Long cursoId;
    private String cursoCodigo;
    private String cursoNombre;
    private Integer creditos;
    private BigDecimal costo;
}