package pe.edu.upeu.MatriculaBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.MatriculaBackend.dto.reporte.MatriculadosPorCursoDTO;
import pe.edu.upeu.MatriculaBackend.entity.Matricula;
import pe.edu.upeu.MatriculaBackend.enums.EstadoMatricula;

import java.util.List;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    boolean existsByEstudianteIdAndPeriodoAndEstado(Long estudianteId, String periodo, EstadoMatricula estado);
    boolean existsByEstudianteId(Long estudianteId);
    List<Matricula> findByEstudianteIdOrderByFechaDesc(Long estudianteId);
    @Query("""
        SELECT new pe.edu.upeu.MatriculaBackend.dto.reporte.MatriculadosPorCursoDTO(
            d.curso.codigo,
            d.curso.nombre,
            COUNT(DISTINCT m.id),
            SUM(d.costo)
        )
        FROM Matricula m
        JOIN m.detalles d
        WHERE m.periodo = :periodo
          AND (:carreraId IS NULL OR d.curso.carrera.id = :carreraId)
          AND m.estado = :estado
        GROUP BY d.curso.codigo, d.curso.nombre
    """)
    List<MatriculadosPorCursoDTO> reporteMatriculadosPorCurso(@Param("periodo") String periodo,
                                                              @Param("estado") EstadoMatricula estado,
                                                              @Param("carreraId") Long carreraId);

    @Query("SELECT COUNT(d) > 0 FROM Matricula m JOIN m.detalles d WHERE d.curso.id = :cursoId")
    boolean existsDetalleByCursoId(@Param("cursoId") Long cursoId);
}