package com.b4w.b4wback.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String name;
    private String lastName;
    private String email;
    private int phoneNumber;
    private String password;
}
