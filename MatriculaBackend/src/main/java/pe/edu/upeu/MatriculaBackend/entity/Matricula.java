package pe.edu.upeu.MatriculaBackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "matriculas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Matricula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private String periodo;

    @ManyToOne
    @JoinColumn(
            name="estudiante_id",
            nullable = false
    )
    private Estudiante estudiante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMatricula estado;

    @Column(nullable = false)
    private Integer totalCreditos;

    @Column(nullable = false,precision = 12, scale = 2)
    private BigDecimal montoTotal;

    @OneToMany(
            mappedBy = "matricula   ",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DetalleMatricula> detalles = new ArrayList<>();

    @PrePersist
    public void prePersist() {

        if (fecha == null) {
            fecha = LocalDateTime.now();
        }

        if (estado == null) {
            estado = EstadoMatricula.REGISTRADA;
        }
    }

    public void agregarDetalle(DetalleMatricula detalle) {
        detalles.add(detalle);
        detalle.setMatricula(this);
    }
}
