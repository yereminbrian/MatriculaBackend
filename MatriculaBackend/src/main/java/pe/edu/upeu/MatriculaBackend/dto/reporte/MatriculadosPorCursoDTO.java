package pe.edu.upeu.MatriculaBackend.dto.reporte;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatriculadosPorCursoDTO {
    private String codigo;
    private String curso;
    private Long matriculados;
    private BigDecimal montoRecaudado;
}