package example.best_project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Evento")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idEvento")
    private Integer idEvento;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 100, message = "El título debe tener entre 5 y 100 caracteres")
    private String titulo;

    @Column(length = 300)
    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @Column(name = "fecha_evento", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Debes seleccionar una fecha")
    private LocalDate fechaEvento;

    @Column(name = "hora_inicio", nullable = false)
    @DateTimeFormat(pattern = "HH:mm")
    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    @DateTimeFormat(pattern = "HH:mm")
    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    @Column(nullable = false, length = 50)
    private String estado = "Activo";

    @Column(name = "lugar_evento", nullable = false, length = 100)
    @NotNull(message = "El lugar es obligatorio")
    private String lugarEvento;

    @Column(nullable = false)
    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El precio no puede ser negativo")
    private BigDecimal precio;

    @Column(nullable = false)
    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "Debe haber al menos 1 espacio")
    private Integer capacidad;

    // ESTO SE DEBE CAMBIAR XD
    @Lob
    @Column(name = "imagen", columnDefinition = "LONGBLOB")
    private byte[] imagen;

    @ManyToOne
    @JoinColumn(name = "idCategoria", nullable = false)
    @NotNull(message = "Selecciona una categoría")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario organizador;

}