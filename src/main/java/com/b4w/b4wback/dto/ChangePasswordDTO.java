package com.b4w.b4wback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO {
    private String email;
    @NotBlank(message = "password can't be blank")
    @Size(min = 8, message = "Password length must be of 8 characters or more")
    @Pattern.List({
            @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit"),
            @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter"),
            @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter"),
    })
    private String password;
}
