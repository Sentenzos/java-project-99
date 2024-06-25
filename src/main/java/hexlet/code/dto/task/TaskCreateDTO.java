package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCreateDTO {
    @Size(min = 1)
    @NotBlank
    private String title;
    private Integer index;
    private String content;
    @NotBlank
    private String status;
    private Long assignee_id;
}
