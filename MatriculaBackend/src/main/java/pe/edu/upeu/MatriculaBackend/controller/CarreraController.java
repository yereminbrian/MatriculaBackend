package pe.edu.upeu.MatriculaBackend.controller;

import jakarta.validation.Valid;
import pe.edu.upeu.MatriculaBackend.dto.CarreraRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.CarreraResponseDTO;
import pe.edu.upeu.MatriculaBackend.dto.CursoResponseDTO;
import pe.edu.upeu.MatriculaBackend.service.service.CarreraService;
import pe.edu.upeu.MatriculaBackend.service.service.CursoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carreras")
public class CarreraController {

    private final CarreraService carreraService;
    private final CursoService cursoService;

    public CarreraController(CarreraService carreraService, CursoService cursoService) {
        this.carreraService = carreraService;
        this.cursoService = cursoService;
    }

    @PostMapping
    public ResponseEntity<CarreraResponseDTO> crear(@Valid @RequestBody CarreraRequestDTO request) {
        CarreraResponseDTO creado = carreraService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<CarreraResponseDTO>> listar() {
        return ResponseEntity.ok(carreraService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarreraResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(carreraService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarreraResponseDTO> actualizar(@PathVariable Long id,
                                                         @Valid @RequestBody CarreraRequestDTO request) {
        return ResponseEntity.ok(carreraService.actualizar(id, request));
    }

    // Eliminar una carrera con cursos asociados debe responder 409 (lo decide el servicio)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        carreraService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // RF-03: cursos de una carrera
    @GetMapping("/{id}/cursos")
    public ResponseEntity<List<CursoResponseDTO>> cursosDeLaCarrera(@PathVariable Long id) {
        return ResponseEntity.ok(cursoService.listarPorCarrera(id));
    }
}