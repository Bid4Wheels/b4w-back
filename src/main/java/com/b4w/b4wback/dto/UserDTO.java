package com.b4w.b4wback.dto;


import com.b4w.b4wback.model.User;
import lombok.*;


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

    public UserDTO(User user){
        name = user.getName();
        lastName = user.getLastName();
        email = user.getEmail();
        phoneNumber = user.getPhoneNumber();
    }
}
