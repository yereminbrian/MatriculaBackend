package pe.edu.upeu.MatriculaBackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.MatriculaBackend.dto.CursoRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.CursoResponseDTO;
import pe.edu.upeu.MatriculaBackend.entity.Carrera;
import pe.edu.upeu.MatriculaBackend.entity.Curso;
import pe.edu.upeu.MatriculaBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.MatriculaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.MatriculaBackend.repository.CarreraRepository;
import pe.edu.upeu.MatriculaBackend.repository.CursoRepository;
import pe.edu.upeu.MatriculaBackend.repository.MatriculaRepository;
import pe.edu.upeu.MatriculaBackend.service.service.CursoService;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CursoServiceImpl implements CursoService {

    private static final Set<String> CAMPOS_ORDEN = Set.of("nombre", "creditos", "vacantes");

    private final CursoRepository cursoRepository;
    private final CarreraRepository carreraRepository;
    private final MatriculaRepository matriculaRepository;

    @Override
    @Transactional
    public CursoResponseDTO crear(CursoRequestDTO request) {
        Carrera carrera = buscarCarrera(request.getCarreraId());
        if (cursoRepository.existsByCodigo(request.getCodigo())) {
            throw conflicto("Ya existe un curso con el código " + request.getCodigo());
        }
        Curso curso = new Curso();
        aplicar(curso, request, carrera);
        Curso guardado = cursoRepository.save(curso);
        log.info("Curso creado: id={}, codigo={}", guardado.getId(), guardado.getCodigo());
        return toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public CursoResponseDTO obtenerPorId(Long id) {
        return toResponse(buscarEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> listar() {
        return cursoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public CursoResponseDTO actualizar(Long id, CursoRequestDTO request) {
        Curso curso = buscarEntidad(id);
        Carrera carrera = buscarCarrera(request.getCarreraId());
        if (cursoRepository.existsByCodigoAndIdNot(request.getCodigo(), id)) {
            throw conflicto("Ya existe otro curso con el código " + request.getCodigo());
        }
        aplicar(curso, request, carrera);
        log.info("Curso actualizado: id={}", id);
        return toResponse(curso);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Curso curso = buscarEntidad(id);
        if (matriculaRepository.existsDetalleByCursoId(id)) {
            throw conflicto("No se puede eliminar el curso: tiene matrículas asociadas");
        }
        cursoRepository.delete(curso);
        log.info("Curso eliminado: id={}", id);
    }


    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> listarPorCarrera(Long carreraId) {
        buscarCarrera(carreraId);
        return cursoRepository.findByCarreraId(carreraId).stream().map(this::toResponse).toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> buscar(String nombre, Long carreraId, Integer ciclo,
                                         Boolean conVacantes, String orden, String dir) {
        String campo = (orden == null || orden.isBlank()) ? "nombre" : orden.trim().toLowerCase();
        if (!CAMPOS_ORDEN.contains(campo)) {
            throw new IllegalArgumentException(
                    "Campo de orden inválido: '" + orden + "'. Valores permitidos: nombre, creditos, vacantes");
        }
        if (dir != null && !dir.isBlank()
                && !dir.equalsIgnoreCase("asc") && !dir.equalsIgnoreCase("desc")) {
            throw new IllegalArgumentException("Dirección inválida: '" + dir + "'. Valores permitidos: asc, desc");
        }
        Sort.Direction direccion = "desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Specification<Curso> spec = (root, query, cb) -> cb.conjunction();

        if (nombre != null && !nombre.isBlank()) {
            String patron = "%" + nombre.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("nombre")), patron));
        }
        if (carreraId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("carrera").get("id"), carreraId));
        }
        if (ciclo != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("ciclo"), ciclo));
        }
        if (conVacantes != null) {
            spec = spec.and((root, query, cb) -> conVacantes
                    ? cb.greaterThan(root.get("vacantes"), 0)
                    : cb.equal(root.get("vacantes"), 0));
        }

        return cursoRepository.findAll(spec, Sort.by(direccion, campo))
                .stream().map(this::toResponse).toList();
    }



    private Curso buscarEntidad(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado con id " + id));
    }

    private Carrera buscarCarrera(Long carreraId) {
        return carreraRepository.findById(carreraId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrera no encontrada con id " + carreraId));
    }

    private void aplicar(Curso curso, CursoRequestDTO r, Carrera carrera) {
        curso.setCodigo(r.getCodigo());
        curso.setNombre(r.getNombre());
        curso.setCreditos(r.getCreditos());
        curso.setCiclo(r.getCiclo());
        curso.setVacantes(r.getVacantes());
        curso.setEstado(r.getEstado() != null ? r.getEstado() : Boolean.TRUE);
        curso.setCarrera(carrera);
    }

    private CursoResponseDTO toResponse(Curso c) {
        return CursoResponseDTO.builder()
                .id(c.getId())
                .codigo(c.getCodigo())
                .nombre(c.getNombre())
                .creditos(c.getCreditos())
                .ciclo(c.getCiclo())
                .vacantes(c.getVacantes())
                .estado(c.getEstado())
                .carreraId(c.getCarrera().getId())
                .carreraNombre(c.getCarrera().getNombre())
                .build();
    }

    private ReglaNegocioException conflicto(String mensaje) {
        log.warn("Regla de negocio: {}", mensaje);
        return new ReglaNegocioException(mensaje);
    }
}