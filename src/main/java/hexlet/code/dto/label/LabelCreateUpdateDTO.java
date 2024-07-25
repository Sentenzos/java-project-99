package hexlet.code.dto.label;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;

@Data
public class LabelCreateUpdateDTO {
    @NotBlank
    @Size(min = 3)
    @Column(unique = true)
    private JsonNullable<String> name;
}
