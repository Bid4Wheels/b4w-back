package com.b4w.b4wback.model;

import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.ModifyUserDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="app_user")
public class User implements UserDetails {
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
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    private Integer passwordCode;

    private boolean isDeleted = false;

    public User(CreateUserDTO createUserDTO){
        name = createUserDTO.getName();
        lastName = createUserDTO.getLastName();
        email = createUserDTO.getEmail();
        phoneNumber = createUserDTO.getPhoneNumber();
        password = createUserDTO.getPassword();
    }


    public void modifyUser(ModifyUserDTO userDTO){
        name = userDTO.getName();
        lastName = userDTO.getLastName();
        phoneNumber = userDTO.getPhoneNumber();
    }


    @OneToMany(mappedBy = "user",cascade = CascadeType.PERSIST)
    private List<Auction> auctionList;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
