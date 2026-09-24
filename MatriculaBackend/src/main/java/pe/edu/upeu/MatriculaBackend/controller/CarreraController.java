package pe.edu.upeu.MatriculaBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.MatriculaBackend.dto.CarreraRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.CarreraResponseDTO;
import pe.edu.upeu.MatriculaBackend.dto.CursoResponseDTO;
import pe.edu.upeu.MatriculaBackend.service.service.CarreraService;
import pe.edu.upeu.MatriculaBackend.service.service.CursoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carreras")
@Tag(name = "Carreras", description = "API pentru gestionarea carierelor universitare (RF-02 și RF-03)")
public class CarreraController {

    private final CarreraService carreraService;
    private final CursoService cursoService;

    public CarreraController(CarreraService carreraService, CursoService cursoService) {
        this.carreraService = carreraService;
        this.cursoService = cursoService;
    }

    @PostMapping
    @Operation(summary = "Înregistrare carieră nouă (RF-02)")
    public ResponseEntity<CarreraResponseDTO> crear(@Valid @RequestBody CarreraRequestDTO request) {
        CarreraResponseDTO creado = carreraService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    @Operation(summary = "Listarea tuturor carierelor")
    public ResponseEntity<List<CarreraResponseDTO>> listar() {
        return ResponseEntity.ok(carreraService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obținerea unei cariere după ID")
    public ResponseEntity<CarreraResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(carreraService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizarea unei cariere existente")
    public ResponseEntity<CarreraResponseDTO> actualizar(@PathVariable Long id,
                                                         @Valid @RequestBody CarreraRequestDTO request) {
        return ResponseEntity.ok(carreraService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Ștergerea unei cariere după ID")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        carreraService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/cursos")
    @Operation(summary = "Listarea cursurilor asociate unei cariere (RF-03)")
    public ResponseEntity<List<CursoResponseDTO>> cursosDeLaCarrera(@PathVariable Long id) {
        return ResponseEntity.ok(carreraService.listarCursosPorCarrera(id));
    }
}