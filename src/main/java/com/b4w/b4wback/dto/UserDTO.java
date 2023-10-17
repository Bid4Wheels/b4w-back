package com.b4w.b4wback.dto;


import com.b4w.b4wback.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;



@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/*
Class used to respond with the data of the user
 */
public class UserDTO {
    private long id;
    private String name;
    private String lastName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String phoneNumber;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String imgURL;
    public UserDTO(User user){
        id=user.getId();
        name = user.getName();
        lastName = user.getLastName();
        email = user.getEmail();
        phoneNumber = user.getPhoneNumber();
    }
}
