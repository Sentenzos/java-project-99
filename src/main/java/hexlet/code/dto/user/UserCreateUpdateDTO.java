package hexlet.code.dto.user;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;

@Data
public class UserCreateUpdateDTO {

    private JsonNullable<String> firstName;


    private JsonNullable<String> lastName;

    @Email
    @Column(unique = true)
    private JsonNullable<String> email;

    @Size(min = 3)
    @NotBlank
    private JsonNullable<String> password;
}
