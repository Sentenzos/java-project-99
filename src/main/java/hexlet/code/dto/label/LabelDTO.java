package hexlet.code.dto.label;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@Data
public class LabelDTO {
    private Long id;
    private String name;
    private LocalDate createdAt;
}
