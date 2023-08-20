package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.MailService;
import com.b4w.b4wback.service.interfaces.UserService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    
    private final MailService mailService;

    public UserServiceImp(UserRepository userRepository, MailService mailService) {
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    @Override
    public User createUser(CreateUserDTO createUserDTO) {
        mailService.sendMail(createUserDTO.getEmail(), "Welcome", "Welcome to our app");
        return userRepository.save(new User(createUserDTO));
    }
}
