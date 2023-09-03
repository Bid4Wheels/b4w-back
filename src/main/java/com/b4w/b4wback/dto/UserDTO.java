package com.b4w.b4wback.dto;


import com.b4w.b4wback.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.net.URL;


@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
/*
Class used to respond with the data of the user
 */
public class UserDTO {
    private String name;
    private String lastName;
    private String email;
    private String phoneNumber;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String imgURL;
    public UserDTO(User user){
        name = user.getName();
        lastName = user.getLastName();
        email = user.getEmail();
        phoneNumber = user.getPhoneNumber();
    }
}
