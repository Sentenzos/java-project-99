package hexlet.code.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Table(name = "labels")
@EntityListeners(AuditingEntityListener.class)
public class Label {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @NotBlank
    @Size(min = 3)
    @Column(unique = true)
    private String name;
    @ManyToMany
    private Set<Task> tasks;
    @CreatedDate
    private LocalDate createdAt;
}
