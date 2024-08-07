package hexlet.code.mapper;

import hexlet.code.dto.user.UserCreateUpdateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.BeforeMapping;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Mapping(target = "passwordDigest", source = "password")
    public abstract User map(UserCreateUpdateDTO userCreateUpdateDTO);
    public abstract UserDTO map(User user);
    @InheritConfiguration
    public abstract void update(UserCreateUpdateDTO updateDTO, @MappingTarget User user);

    @BeforeMapping
    public void encryptPassword(UserCreateUpdateDTO updateDTO) {
        if (updateDTO.getPassword() != null) {
            String password = updateDTO.getPassword().get();
            updateDTO.setPassword(JsonNullable.of(passwordEncoder.encode(password)));
        }
    }
}
