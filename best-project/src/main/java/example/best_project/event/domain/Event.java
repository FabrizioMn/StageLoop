package example.best_project.event.domain;

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

import example.best_project.user.domain.User;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_event")
    private Integer id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @Column(length = 300)
    @NotBlank(message = "Description is required")
    private String description;

    @Column(name = "event_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "You must select a date")
    private LocalDate eventDate;

    @Column(name = "start_time", nullable = false)
    @DateTimeFormat(pattern = "HH:mm")
    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    @DateTimeFormat(pattern = "HH:mm")
    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @Column(nullable = false, length = 50)
    private String status = "Active";

    @Column(name = "location", nullable = false, length = 100)
    @NotNull(message = "Location is required")
    private String location;

    @Column(nullable = false)
    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price cannot be negative")
    private BigDecimal price;

    @Column(nullable = false)
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "There must be at least 1 spot available")
    private Integer capacity;

    // TODO: THIS NEEDS TO BE CHANGED XD
    @Lob
    @Column(name = "image", columnDefinition = "LONGBLOB")
    private byte[] image;

    @ManyToOne
    @JoinColumn(name = "id_category", nullable = false)
    @NotNull(message = "Select a category")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User organizer;

}