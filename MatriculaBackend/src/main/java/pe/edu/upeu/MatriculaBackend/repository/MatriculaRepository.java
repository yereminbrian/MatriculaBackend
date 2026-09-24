package pe.edu.upeu.MatriculaBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.MatriculaBackend.entity.Matricula;

import java.util.List;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    boolean existsByEstudianteIdAndPeriodoAndEstado(Long estudianteId, String periodo, pe.edu.upeu.MatriculaBackend.enums.EstadoMatricula estado);
    List<Matricula> findByEstudianteIdOrderByFechaDesc(Long estudianteId);
    @Query("""
        SELECT d.curso.codigo AS codigo,
               d.curso.nombre AS curso,
               COUNT(DISTINCT m.id) AS matriculados,
               SUM(d.costo) AS montoRecaudado
        FROM Matricula m
        JOIN m.detalles d
        WHERE m.periodo = :periodo
          AND d.curso.carrera.id = :carreraId
          AND m.estado = pe.edu.upeu.MatriculaBackend.enums.EstadoMatricula.REGISTRADA
        GROUP BY d.curso.codigo, d.curso.nombre
    """)
    List<Object[]> obtenerReporteMatriculadosPorCurso(@Param("periodo") String periodo, @Param("carreraId") Long carreraId);
}