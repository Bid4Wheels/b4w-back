package com.b4w.b4wback.model;

import com.b4w.b4wback.dto.CreateUserDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "app_user")
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

    @Column(nullable = false)
    private Integer phoneNumber;

    @Column(nullable = false)
    private String password;

    public User(CreateUserDTO createUserDTO){
        name = createUserDTO.getName();
        lastName = createUserDTO.getLastName();
        email = createUserDTO.getEmail();
        phoneNumber = createUserDTO.getPhoneNumber();
        password = createUserDTO.getPassword();
    }

}
