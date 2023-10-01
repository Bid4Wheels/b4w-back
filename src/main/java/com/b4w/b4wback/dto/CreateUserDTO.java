package com.b4w.b4wback.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDTO {
    @NotBlank(message = "name can't be blank")
    private String name;

    @NotBlank(message = "last name can't be blank")
    private String lastName;

    @Email(regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$")
    @NotBlank(message = "email can't be blank")
    private String email;

    @NotNull(message = "phone number can't be blank")
    @Size(min = 14, message = "invalid phone number size, require at least 14")
    private String phoneNumber;

    @NotBlank(message = "password can't be blank")
    @Size(min = 8, message = "Password length must be of 8 characters or more")
    @Pattern.List({
            @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit"),
            @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter"),
            @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter"),
    })
    private String password;
}
