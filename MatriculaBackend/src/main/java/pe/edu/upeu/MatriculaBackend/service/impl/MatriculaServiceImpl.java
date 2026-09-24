package pe.edu.upeu.MatriculaBackend.service.impl;

package pe.edu.upeu.MatriculaBackend.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.MatriculaBackend.dto.DetalleMatriculaRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.DetalleMatriculaResponseDTO;
import pe.edu.upeu.MatriculaBackend.dto.MatriculaRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.MatriculaResponseDTO;
import pe.edu.upeu.MatriculaBackend.dto.MatriculadosPorCursoDTO;
import pe.edu.upeu.MatriculaBackend.entity.Curso;
import pe.edu.upeu.MatriculaBackend.entity.DetalleMatricula;
import pe.edu.upeu.MatriculaBackend.entity.Estudiante;
import pe.edu.upeu.MatriculaBackend.entity.Matricula;
import pe.edu.upeu.MatriculaBackend.enums.EstadoMatricula;
import pe.edu.upeu.MatriculaBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.MatriculaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.MatriculaBackend.repository.CarreraRepository;
import pe.edu.upeu.MatriculaBackend.repository.CursoRepository;
import pe.edu.upeu.MatriculaBackend.repository.EstudianteRepository;
import pe.edu.upeu.MatriculaBackend.repository.MatriculaRepository;
import pe.edu.upeu.MatriculaBackend.service.service.MatriculaService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class MatriculaServiceImpl implements MatriculaService {

    private static final int MAX_CREDITOS = 20;                        // RN-04

    private final MatriculaRepository matriculaRepository;
    private final EstudianteRepository estudianteRepository;
    private final CursoRepository cursoRepository;
    private final CarreraRepository carreraRepository;
    private final BigDecimal costoCredito;                             // matricula.costo-credito: 120.00

    public MatriculaServiceImpl(MatriculaRepository matriculaRepository,
                                EstudianteRepository estudianteRepository,
                                CursoRepository cursoRepository,
                                CarreraRepository carreraRepository,
                                @Value("${matricula.costo-credito}") BigDecimal costoCredito) {
        this.matriculaRepository = matriculaRepository;
        this.estudianteRepository = estudianteRepository;
        this.cursoRepository = cursoRepository;
        this.carreraRepository = carreraRepository;
        this.costoCredito = costoCredito;
    }

    @Override
    @Transactional
    public MatriculaResponseDTO crear(MatriculaRequestDTO request) {
        Estudiante estudiante = estudianteRepository.findById(request.getEstudianteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estudiante no encontrado con id " + request.getEstudianteId()));

        // RN-01 (estudiante): solo estudiantes activos
        if (!Boolean.TRUE.equals(estudiante.getEstado())) {
            throw conflicto("RN-01: el estudiante " + estudiante.getCodigo() + " está inactivo");
        }

        // RN-03: una sola matrícula REGISTRADA por estudiante y periodo
        if (matriculaRepository.existsByEstudianteIdAndPeriodoAndEstado(
                estudiante.getId(), request.getPeriodo(), EstadoMatricula.REGISTRADA)) {
            throw conflicto("RN-03: el estudiante ya tiene una matrícula REGISTRADA en el periodo "
                    + request.getPeriodo());
        }

        Matricula matricula = new Matricula();
        matricula.setFecha(LocalDateTime.now());                       // la asigna el servidor
        matricula.setPeriodo(request.getPeriodo());
        matricula.setEstudiante(estudiante);
        matricula.setEstado(EstadoMatricula.REGISTRADA);

        Set<Long> cursosVistos = new HashSet<>();
        int totalCreditos = 0;
        BigDecimal montoTotal = BigDecimal.ZERO;

        for (DetalleMatriculaRequestDTO item : request.getDetalles()) {
            if (!cursosVistos.add(item.getCursoId())) {
                throw conflicto("El curso con id " + item.getCursoId() + " está repetido en la solicitud");
            }
            Curso curso = cursoRepository.findById(item.getCursoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Curso no encontrado con id " + item.getCursoId()));


            if (!Boolean.TRUE.equals(curso.getEstado())) {
                throw conflicto("RN-01: el curso " + curso.getCodigo() + " está inactivo");
            }
            if (!curso.getCarrera().getId().equals(estudiante.getCarrera().getId())) {
                throw conflicto("RN-01: el curso " + curso.getCodigo()
                        + " no pertenece a la carrera del estudiante");
            }


            int nuevoTotal = totalCreditos + curso.getCreditos();
            if (nuevoTotal > MAX_CREDITOS) {
                throw conflicto("RN-04: la matrícula superaría los " + MAX_CREDITOS
                        + " créditos (" + nuevoTotal + ")");
            }


            if (curso.getVacantes() <= 0) {
                throw conflicto("RN-02: el curso " + curso.getCodigo() + " no tiene vacantes");
            }

            curso.setVacantes(curso.getVacantes() - 1);


            BigDecimal costo = calcularCosto(curso.getCreditos());
            DetalleMatricula detalle = new DetalleMatricula();
            detalle.setCurso(curso);
            detalle.setCreditos(curso.getCreditos());
            detalle.setCosto(costo);
            matricula.agregarDetalle(detalle);

            totalCreditos = nuevoTotal;
            montoTotal = montoTotal.add(costo);
        }

        matricula.setTotalCreditos(totalCreditos);
        matricula.setMontoTotal(montoTotal.setScale(2, RoundingMode.HALF_UP));

        Matricula guardada = matriculaRepository.save(matricula);      // cascade ALL guarda los detalles
        log.info("Matrícula registrada: id={}, estudianteId={}, periodo={}, créditos={}, monto={}",
                guardada.getId(), estudiante.getId(), guardada.getPeriodo(),
                guardada.getTotalCreditos(), guardada.getMontoTotal());
        return toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public MatriculaResponseDTO obtenerPorId(Long id) {
        return toResponse(buscarEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatriculaResponseDTO> listar() {
        return matriculaRepository.findAll().stream().map(this::toResponse).toList();
    }

    // RF-05: PATCH /api/v1/matriculas/{id}/anular
    @Override
    @Transactional
    public MatriculaResponseDTO anular(Long id) {
        Matricula matricula = buscarEntidad(id);
        if (matricula.getEstado() == EstadoMatricula.ANULADA) {
            throw conflicto("La matrícula " + id + " ya está anulada");
        }
        for (DetalleMatricula detalle : matricula.getDetalles()) {
            Curso curso = detalle.getCurso();
            curso.setVacantes(curso.getVacantes() + 1);                // devuelve la vacante
        }
        matricula.setEstado(EstadoMatricula.ANULADA);
        log.info("Matrícula anulada: id={}, vacantes devueltas={}", id, matricula.getDetalles().size());
        return toResponse(matricula);
    }


    @Override
    @Transactional(readOnly = true)
    public List<MatriculadosPorCursoDTO> reporteMatriculadosPorCurso(String periodo, Long carreraId) {
        log.info("Reporte matriculados por curso: periodo={}, carreraId={}", periodo, carreraId);
        if (carreraId != null && !carreraRepository.existsById(carreraId)) {
            throw new RecursoNoEncontradoException("Carrera no encontrada con id " + carreraId);
        }
        return matriculaRepository.reporteMatriculadosPorCurso(periodo, EstadoMatricula.REGISTRADA, carreraId);
    }


    private Matricula buscarEntidad(Long id) {
        return matriculaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con id " + id));
    }


    private BigDecimal calcularCosto(int creditos) {
        return costoCredito.multiply(BigDecimal.valueOf(creditos)).setScale(2, RoundingMode.HALF_UP);
    }

    private MatriculaResponseDTO toResponse(Matricula m) {
        List<DetalleMatriculaResponseDTO> detalles = m.getDetalles().stream()
                .map(d -> DetalleMatriculaResponseDTO.builder()
                        .id(d.getId())
                        .cursoId(d.getCurso().getId())
                        .cursoCodigo(d.getCurso().getCodigo())
                        .cursoNombre(d.getCurso().getNombre())
                        .creditos(d.getCreditos())
                        .costo(d.getCosto())
                        .build())
                .toList();
        return MatriculaResponseDTO.builder()
                .id(m.getId())
                .fecha(m.getFecha())
                .periodo(m.getPeriodo())
                .estudianteId(m.getEstudiante().getId())
                .estudianteNombre(m.getEstudiante().getNombres() + " " + m.getEstudiante().getApellidos())
                .estado(m.getEstado())
                .totalCreditos(m.getTotalCreditos())
                .montoTotal(m.getMontoTotal())
                .detalles(detalles)
                .build();
    }

    private ReglaNegocioException conflicto(String mensaje) {
        log.warn("Regla de negocio: {}", mensaje);
        return new ReglaNegocioException(mensaje);
    }
}
