package pe.edu.upeu.MatriculaBackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.MatriculaBackend.dto.CarreraRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.CarreraResponseDTO;
import pe.edu.upeu.MatriculaBackend.dto.CursoResponseDTO;
import pe.edu.upeu.MatriculaBackend.entity.Carrera;
import pe.edu.upeu.MatriculaBackend.entity.Curso;
import pe.edu.upeu.MatriculaBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.MatriculaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.MatriculaBackend.repository.CarreraRepository;
import pe.edu.upeu.MatriculaBackend.repository.CursoRepository;
import pe.edu.upeu.MatriculaBackend.repository.EstudianteRepository;
import pe.edu.upeu.MatriculaBackend.service.service.CarreraService;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarreraServiceImpl implements CarreraService {

    private final CarreraRepository carreraRepository;
    private final CursoRepository cursoRepository;
    private final EstudianteRepository estudianteRepository;

    @Override
    @Transactional
    public CarreraResponseDTO crear(CarreraRequestDTO request) {
        String nombre = limpiar(request.getNombre());
        if (carreraRepository.existsByNombreIgnoreCase(nombre)) {
            throw conflicto("Ya existe una carrera con el nombre '" + nombre + "'");
        }
        Carrera carrera = new Carrera();
        aplicar(carrera, request, nombre);
        Carrera guardada = carreraRepository.save(carrera);
        log.info("Carrera creada: id={}, nombre={}", guardada.getId(), guardada.getNombre());
        return toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public CarreraResponseDTO obtenerPorId(Long id) {
        return toResponse(buscarEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarreraResponseDTO> listarTodos() {
        return carreraRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CarreraResponseDTO actualizar(Long id, CarreraRequestDTO request) {
        Carrera carrera = buscarEntidad(id);
        String nombre = limpiar(request.getNombre());
        if (carreraRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw conflicto("Ya existe otra carrera con el nombre '" + nombre + "'");
        }
        aplicar(carrera, request, nombre);
        log.info("Carrera actualizada: id={}", id);
        return toResponse(carrera);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Carrera carrera = buscarEntidad(id);

        if (cursoRepository.existsByCarreraId(id)) {
            throw conflicto("No se puede eliminar la carrera: tiene cursos asociados");
        }
        if (estudianteRepository.existsByCarreraId(id)) {
            throw conflicto("No se puede eliminar la carrera: tiene estudiantes asociados");
        }

        carreraRepository.delete(carrera);
        log.info("Carrera eliminada: id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> listarCursosPorCarrera(Long carreraId) {
        buscarEntidad(carreraId);
        return cursoRepository.findByCarreraId(carreraId).stream()
                .map(this::toCursoResponse)
                .toList();
    }

    private Carrera buscarEntidad(Long id) {
        return carreraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrera no encontrada con id " + id));
    }

    private void aplicar(Carrera carrera, CarreraRequestDTO request, String nombre) {
        carrera.setNombre(nombre);
        carrera.setDescripcion(request.getDescripcion());
        carrera.setEstado(Boolean.TRUE);
    }

    private CarreraResponseDTO toResponse(Carrera c) {
        return CarreraResponseDTO.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .descripcion(c.getDescripcion())
                .estado(c.getEstado())
                .build();
    }

    private CursoResponseDTO toCursoResponse(Curso c) {
        return CursoResponseDTO.builder()
                .id(c.getId())
                .codigo(c.getCodigo())
                .nombre(c.getNombre())
                .creditos(c.getCreditos())
                .ciclo(c.getCiclo())
                .vacantes(c.getVacantes())
                .estado(c.getEstado())
                .carreraId(c.getCarrera() != null ? c.getCarrera().getId() : null)
                .carreraNombre(c.getCarrera() != null ? c.getCarrera().getNombre() : null)
                .build();
    }

    private String limpiar(String texto) {
        return texto == null ? null : texto.trim().replaceAll("\\s+", " ");
    }

    private ReglaNegocioException conflicto(String mensaje) {
        log.warn("Regla de negocio violada: {}", mensaje);
        return new ReglaNegocioException(mensaje);
    }
}