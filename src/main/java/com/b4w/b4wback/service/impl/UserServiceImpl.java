package com.b4w.b4wback.service.impl;

import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.service.MailService;
import com.b4w.b4wback.service.UserService;
import com.b4w.b4wback.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;


import java.util.Optional;

@Service
@Validated
public class UserServiceImpl implements UserService {
    private final UserRepository UserRepository;
    private final MailService mailService;

    public UserServiceImpl(UserRepository UserRepository, MailService mailService) {
        this.UserRepository = UserRepository;
        this.mailService = mailService;
    }
    @Override
    public CreateUserDTO createUser(CreateUserDTO user) {
        User newUser = User.builder().
                name(user.getName()).
                lastName(user.getLastName()).
                email(user.getEmail()).
                phoneNumber(user.getPhoneNumber()).
                password(user.getPassword()).
                build();
        mailService.sendMail(user.getEmail(), "Welcome", "Welcome to our app");
        return UserRepository.save(newUser).toDTO();
    }


    @Override
    public Optional<User> getUserById(Long id) {
        Optional<User> user = UserRepository.findById(id);
        return user;
    }
}
