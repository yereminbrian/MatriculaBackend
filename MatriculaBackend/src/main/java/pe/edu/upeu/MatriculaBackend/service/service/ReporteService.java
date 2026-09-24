package pe.edu.upeu.MatriculaBackend.service.service;

import pe.edu.upeu.MatriculaBackend.dto.reporte.MatriculadosPorCursoDTO;
import java.util.List;

public interface ReporteService {
    List<MatriculadosPorCursoDTO> matriculadosPorCurso(String periodo, Long carreraId);
}