package hexlet.code.mapper;

import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelCreateUpdateDTO;
import hexlet.code.model.Label;
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
public abstract class LabelMapper {
    public abstract Label map(LabelCreateUpdateDTO labelCreateDTO);
    public abstract LabelDTO map(Label label);
    @InheritConfiguration
    public abstract void update(LabelCreateUpdateDTO updateDTO, @MappingTarget Label label);
}
