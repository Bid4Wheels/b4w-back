package com.b4w.b4wback.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyUserDTO {
    @NotBlank(message = "name can't be blank")
    private String name;

    @NotBlank(message = "last name can't be blank")
    private String lastName;

    @NotNull(message = "phone number can't be blank")
    @Size(min = 14, message = "invalid phone number size, require at least 14")
    private String phoneNumber;

    public ModifyUserDTO(String name, String lastName, String phoneNumber){
        this.name = name;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }
}
