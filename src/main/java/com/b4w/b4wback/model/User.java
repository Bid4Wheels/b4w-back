package com.b4w.b4wback.model;

import com.b4w.b4wback.dto.UserDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String lastName;
    @Column(unique = true, nullable = false)
    private String email;
    private int phoneNumber;
    @Column(nullable = false)
    private String password;

    public User(UserDTO userDTO){
        name = userDTO.getName();
        lastName = userDTO.getLastName();
        email = userDTO.getEmail();
        phoneNumber = userDTO.getPhoneNumber();
        password = userDTO.getPassword();
    }
}
