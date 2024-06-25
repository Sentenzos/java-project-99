package hexlet.code.mapper;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.dto.taskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskStatus.TaskStatusDTO;
import hexlet.code.dto.taskStatus.TaskStatusUpdateDTO;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Mapping(target = "assignee", source = "assignee_id")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "name", source = "title")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "getStatusBySlug")
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(target = "assignee_id", source = "assignee.id")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "status", source = "taskStatus.slug")
    public abstract TaskDTO map(Task model);

    @Mapping(target = "assignee", source = "assignee_id")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "name", source = "title")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "getStatusBySlug")
    public abstract void update(TaskUpdateDTO dto, @MappingTarget Task model);

    @Named("getStatusBySlug")
    TaskStatus getStatusBySlug(String status) {
        return taskStatusRepository.findBySlug(status)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Status with slug " + status + " not found"));
    }

}

