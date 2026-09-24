package pe.edu.upeu.MatriculaBackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_matricula")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleMatricula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "matricula_id",
            nullable = false
    )
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "curso_id",
            nullable = false
    )
    private Curso curso;

    @Column(nullable = false)
    private Integer creditos;

    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal costo;
}
