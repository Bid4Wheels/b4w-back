package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.*;
import com.b4w.b4wback.exception.EntityNotFoundException;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.MailService;
import com.b4w.b4wback.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;
import java.util.Random;


@Service
@Validated
public class UserServiceImp implements UserService {
    @Value("${sendMail.Boolean.Value}")
    private boolean sendMail;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
    private static final Random Random = new Random();

    public UserServiceImp(UserRepository userRepository, MailService mailService) {
        this.userRepository = userRepository;
        this.mailService = mailService;
    }
    @Override
    public User createUser(CreateUserDTO createUserDTO) {
        //Added encode method for the password.
        if (createUserDTO.getPassword()==null){
            throw new DataIntegrityViolationException("Password must not be null");
        }
        createUserDTO.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
        String email = createUserDTO.getEmail();
        User newUser = userRepository.save(new User(createUserDTO));
        if (sendMail){
            mailService.sendMail(email,"Welcome to B4W","Welcome to B4W");
            email = "";
        }
        return newUser;
    }
    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return UserDTO.builder().name(user.getName()).lastName(user.getLastName()).email(user.getEmail()).phoneNumber(user.getPhoneNumber()).build();
    }

    @Override
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("User not found"));
    }

    @Override
    public void getPasswordChangerForId(long id, PasswordChangerDTO userDTO) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Integer passwordCode = Random.nextInt(100000, 999999);
        user.setPasswordCode(passwordCode);
        mailService.sendMail(user.getEmail(),"Password change code","Your password change code is: "+ passwordCode);
        userRepository.save(user);
    }

    @Override
    public Boolean getPasswordCode(GetPasswordCodeDTO passwordCodeDTO) {
        User user = userRepository.findByEmail(passwordCodeDTO.getEmail()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return Objects.equals(user.getPasswordCode(), passwordCodeDTO.getPasswordCode());
    }

    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        User user = userRepository.findByEmail(changePasswordDTO.getEmail()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setPassword(changePasswordDTO.getPassword());
        userRepository.save(user);
        user.setPasswordCode(null);
    }
}
