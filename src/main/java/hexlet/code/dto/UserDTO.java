package hexlet.code.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class UserDTO {
    @NotNull
    private Long id;
    private String firstName;
    private String lastName;
    @Email
    private String email;
    private LocalDate createdAt;
}
