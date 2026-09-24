package pe.edu.upeu.MatriculaBackend.controller;

import pe.edu.upeu.MatriculaBackend.dto.reporte.MatriculadosPorCursoDTO;
import pe.edu.upeu.MatriculaBackend.service.service.ReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/matriculados-por-curso")
    public ResponseEntity<List<MatriculadosPorCursoDTO>> matriculadosPorCurso(
            @RequestParam(required = false) String periodo,
            @RequestParam(required = false) Long carreraId) {

        List<MatriculadosPorCursoDTO> reporte = reporteService.matriculadosPorCurso(periodo, carreraId);
        return ResponseEntity.ok(reporte);
    }
}