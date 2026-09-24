package pe.edu.upeu.MatriculaBackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.MatriculaBackend.dto.reporte.MatriculadosPorCursoDTO;
import pe.edu.upeu.MatriculaBackend.enums.EstadoMatricula;
import pe.edu.upeu.MatriculaBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.MatriculaBackend.repository.CarreraRepository;
import pe.edu.upeu.MatriculaBackend.repository.MatriculaRepository;
import pe.edu.upeu.MatriculaBackend.service.service.ReporteService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final MatriculaRepository matriculaRepository;
    private final CarreraRepository carreraRepository;

    // RF-07: GET /api/v1/reportes/matriculados-por-curso?periodo=&carreraId=
    @Override
    @Transactional(readOnly = true)
    public List<MatriculadosPorCursoDTO> matriculadosPorCurso(String periodo, Long carreraId) {
        log.info("Reporte matriculados por curso: periodo={}, carreraId={}", periodo, carreraId);

        // Referencia inexistente -> 404 (no un reporte vacío engañoso)
        if (carreraId != null && !carreraRepository.existsById(carreraId)) {
            throw new RecursoNoEncontradoException("Carrera no encontrada con id " + carreraId);
        }

        // Agregación en la base de datos (JPQL GROUP BY + proyección),
        // solo matrículas REGISTRADA; sin bucles sobre findAll()
        List<MatriculadosPorCursoDTO> resultado =
                matriculaRepository.reporteMatriculadosPorCurso(periodo, EstadoMatricula.REGISTRADA, carreraId);

        log.info("Reporte generado: {} curso(s)", resultado.size());
        return resultado;
    }
}
