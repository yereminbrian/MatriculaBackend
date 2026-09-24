package pe.edu.upeu.MatriculaBackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "cursos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 5, nullable = false)
    @Pattern(regexp = "^[A-Z]{2}\\d{3}$", message = "El código debe tener 2 letras mayúsculas seguidas de 3 números")
    private String codigo;

    @Column(length = 150, nullable = false)
    @Size(min = 3, max = 150)
    private String nombre;

    @Column(nullable = false)
    @Min(value = 1)
    @Max(value = 6)
    private Integer creditos;

    @Column(nullable = false)
    @Min(value = 1)
    @Max(value = 10)
    private Integer ciclo;

    @Column(nullable = false)
    @Min(value = 0)
    private Integer vacantes;

    @Column(nullable = false)
    private Boolean estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_id", nullable = false)
    private Carrera carrera;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificación")
    private LocalDateTime fechaModificacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        if(estado==null){
            estado=true;
        }
    }@PreUpdate
    public void preUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }

}
