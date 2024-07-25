package hexlet.code.mapper;

import hexlet.code.dto.taskStatus.TaskStatusCreateUpdateDTO;
import hexlet.code.dto.taskStatus.TaskStatusDTO;
import hexlet.code.model.TaskStatus;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.MappingTarget;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskStatusMapper {
    public abstract TaskStatus map(TaskStatusCreateUpdateDTO data);
    public abstract TaskStatusDTO map(TaskStatus data);
    @InheritConfiguration
    public abstract void update(TaskStatusCreateUpdateDTO data, @MappingTarget TaskStatus taskStatus);
}
