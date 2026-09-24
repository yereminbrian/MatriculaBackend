package pe.edu.upeu.MatriculaBackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.MatriculaBackend.dto.EstudianteRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.EstudianteResponseDTO;
import pe.edu.upeu.MatriculaBackend.entity.Carrera;
import pe.edu.upeu.MatriculaBackend.entity.Estudiante;
import pe.edu.upeu.MatriculaBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.MatriculaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.MatriculaBackend.repository.CarreraRepository;
import pe.edu.upeu.MatriculaBackend.repository.EstudianteRepository;
import pe.edu.upeu.MatriculaBackend.repository.MatriculaRepository;
import pe.edu.upeu.MatriculaBackend.service.service.EstudianteService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstudianteServiceImpl implements EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final CarreraRepository carreraRepository;
    private final MatriculaRepository matriculaRepository;

    @Override
    @Transactional
    public EstudianteResponseDTO crear(EstudianteRequestDTO request) {
        Carrera carrera = buscarCarrera(request.getCarreraId());      // 404 si no existe
        if (estudianteRepository.existsByCodigo(request.getCodigo())) {
            throw conflicto("Ya existe un estudiante con el código " + request.getCodigo());
        }
        if (estudianteRepository.existsByDni(request.getDni())) {
            throw conflicto("Ya existe un estudiante con el DNI " + request.getDni());
        }
        Estudiante estudiante = new Estudiante();
        aplicar(estudiante, request, carrera);
        Estudiante guardado = estudianteRepository.save(estudiante);
        log.info("Estudiante registrado: id={}, codigo={}", guardado.getId(), guardado.getCodigo());
        return toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public EstudianteResponseDTO obtenerPorId(Long id) {
        return toResponse(buscarEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteResponseDTO> listar() {
        return estudianteRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public EstudianteResponseDTO actualizar(Long id, EstudianteRequestDTO request) {
        Estudiante estudiante = buscarEntidad(id);
        Carrera carrera = buscarCarrera(request.getCarreraId());
        if (estudianteRepository.existsByCodigoAndIdNot(request.getCodigo(), id)) {
            throw conflicto("Ya existe otro estudiante con el código " + request.getCodigo());
        }
        if (estudianteRepository.existsByDniAndIdNot(request.getDni(), id)) {
            throw conflicto("Ya existe otro estudiante con el DNI " + request.getDni());
        }
        aplicar(estudiante, request, carrera);
        log.info("Estudiante actualizado: id={}", id);
        return toResponse(estudiante);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Estudiante estudiante = buscarEntidad(id);
        if (matriculaRepository.existsByEstudianteId(id)) {
            throw conflicto("No se puede eliminar el estudiante: tiene matrículas asociadas");
        }
        estudianteRepository.delete(estudiante);
        log.info("Estudiante eliminado: id={}", id);
    }


    private Estudiante buscarEntidad(Long id) {
        return estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con id " + id));
    }

    private Carrera buscarCarrera(Long carreraId) {
        return carreraRepository.findById(carreraId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrera no encontrada con id " + carreraId));
    }

    private void aplicar(Estudiante e, EstudianteRequestDTO r, Carrera carrera) {
        e.setCodigo(r.getCodigo());
        e.setDni(r.getDni());
        e.setNombres(r.getNombres());
        e.setApellidos(r.getApellidos());
        e.setEmail(r.getEmail());
        e.setEstado(r.getEstado() != null ? r.getEstado() : Boolean.TRUE);
        e.setCarrera(carrera);
    }

    private EstudianteResponseDTO toResponse(Estudiante e) {
        return EstudianteResponseDTO.builder()
                .id(e.getId())
                .codigo(e.getCodigo())
                .dni(e.getDni())
                .nombres(e.getNombres())
                .apellidos(e.getApellidos())
                .email(e.getEmail())
                .estado(e.getEstado())
                .carreraId(e.getCarrera().getId())
                .carreraNombre(e.getCarrera().getNombre())
                .build();
    }

    private ReglaNegocioException conflicto(String mensaje) {
        log.warn("Regla de negocio: {}", mensaje);
        return new ReglaNegocioException(mensaje);
    }
}