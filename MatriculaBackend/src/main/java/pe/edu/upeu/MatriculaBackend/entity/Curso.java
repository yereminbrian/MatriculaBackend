package pe.edu.upeu.MatriculaBackend.entity;

import jakarta.persistence.*;
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

    @Column(nullable = false, length = 50)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false)
    private Integer creditos;

    @Column(nullable = false)
    private Integer ciclo;

    @Column(nullable = false)
    private Integer vacantes;

    @Column(nullable = false)
    private Boolean estado;

    @ManyToOne
    @JoinColumn(
            name="carrera_id",
            nullable = false
    )
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
