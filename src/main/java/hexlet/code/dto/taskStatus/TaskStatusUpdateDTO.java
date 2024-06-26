package hexlet.code.dto.taskStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskStatusUpdateDTO {
    @Size(min = 1)
    @NotBlank
    private String name;
}
