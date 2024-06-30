package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class TaskDTO {
    private Long id;
    private Integer index;
    private String title;
    private String content;
    private String status;
    private Long assignee_id;
    private Set<Long> taskLabelIds;
    private LocalDate createdAt;
}
