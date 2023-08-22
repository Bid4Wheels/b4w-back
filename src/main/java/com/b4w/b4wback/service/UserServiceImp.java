package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.MailService;
import com.b4w.b4wback.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class UserServiceImp implements UserService {
    @Value("${sendMail.Boolean.Value}")
    private boolean sendMail;
    private final UserRepository userRepository;
    
    private final MailService mailService;

    public UserServiceImp(UserRepository userRepository, MailService mailService) {
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    @Override
    public User createUser(CreateUserDTO createUserDTO) {
        User newUser = userRepository.save(new User(createUserDTO));
        if(createUserDTO.getEmail() != null && sendMail){
            mailService.sendMail( createUserDTO.getEmail(), "Welcome", "Welcome to our app");
        }
        return newUser;
    }
}
