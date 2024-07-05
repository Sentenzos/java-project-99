package hexlet.code.mapper;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private LabelRepository labelRepository;

    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "name", source = "title")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "getStatusBySlug")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "getLabels")
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(target = "taskLabelIds", source = "labels", qualifiedByName = "getLabelIds")
    public abstract TaskDTO map(Task model);

    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "name", source = "title")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "getStatusBySlug")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "getLabels")
    public abstract void update(TaskUpdateDTO dto, @MappingTarget Task model);

    @Named("getStatusBySlug")
    TaskStatus getStatusBySlug(String status) {
        return taskStatusRepository.findBySlug(status)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Status with slug " + status + " not found"));
    }

    @Named("getLabelIds")
    Set<Long> getLabelIds(Set<Label> labels) {
        return labels.stream()
                .map(Label::getId)
                .collect(Collectors.toSet());
    }

    @Named("getLabels")
    Set<Label> getLabels(Set<Long> labelIds) {
        return labelRepository.findAll()
                .stream()
                .map(label -> labelIds.contains(label.getId()) ? label : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}

