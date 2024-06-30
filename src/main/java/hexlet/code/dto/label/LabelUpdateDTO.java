package hexlet.code.dto.label;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LabelUpdateDTO {
    @NotBlank
    @Size(min = 3)
    @Column(unique = true)
    private String name;
}
