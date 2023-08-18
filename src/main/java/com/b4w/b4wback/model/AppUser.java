package com.b4w.b4wback.model;

import com.b4w.b4wback.dto.CreateAppUserDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AppUser {
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

    public AppUser(CreateAppUserDTO createUserDTO){
        name = createUserDTO.getName();
        lastName = createUserDTO.getLastName();
        email = createUserDTO.getEmail();
        phoneNumber = createUserDTO.getPhoneNumber();
        password = createUserDTO.getPassword();
    }
    public CreateAppUserDTO toDTO(){
        return CreateAppUserDTO.builder().name(name).lastName(lastName).email(email).phoneNumber(phoneNumber).password(password).build();
    }
}
