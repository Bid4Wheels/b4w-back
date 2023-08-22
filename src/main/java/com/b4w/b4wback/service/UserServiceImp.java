package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.UserDTO;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
    public UserServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public User createUser(CreateUserDTO createUserDTO) {
        //Added encode method for the password.
        if (createUserDTO.getPassword()==null){
            throw new DataIntegrityViolationException("Password must not be null");
        }
        createUserDTO.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
        return userRepository.save(new User(createUserDTO));
    }
    @Override
    public UserDTO getUserById(long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return UserDTO.builder().name(user.get().getName()).lastName(user.get().getLastName()).email(user.get().getEmail()).phoneNumber(user.get().getPhoneNumber()).build();
        } else {
            throw new EntityNotFoundException("User not found");
        }
    }

    @Override
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("User not found"));
    }
}
