package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

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
}
