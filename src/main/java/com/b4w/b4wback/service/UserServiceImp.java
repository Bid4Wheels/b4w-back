package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.ModifyUserDTO;
import com.b4w.b4wback.dto.UserDTO;
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


@Service
@Validated
public class UserServiceImp implements UserService {
    @Value("${sendMail.Boolean.Value}")
    private boolean sendMail;
    private final UserRepository userRepository;

    private final MailService mailService;

    private final PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();

    public UserServiceImp(UserRepository userRepository, MailService mailService) {
        this.userRepository = userRepository;
        this.mailService = mailService;
    }
    @Override
    public User createUser(CreateUserDTO createUserDTO) {
        //Added encode method for the password. If for test
        if (createUserDTO.getPassword()==null){
            throw new DataIntegrityViolationException("Password must not be null");
        }
        else if(createUserDTO.getEmail() != null && sendMail){
            if (userRepository.findByEmail(createUserDTO.getEmail()).isPresent())
                throw new DataIntegrityViolationException("Email already taken");

            mailService.sendMail( createUserDTO.getEmail(), "Welcome", "Welcome to our app");
        }
        createUserDTO.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
        return userRepository.save(new User(createUserDTO));
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
    public void modifyUser(long id, ModifyUserDTO userDTO){
        User userModify = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User not found"));

        userModify.modifyUser(userDTO);
        userRepository.save(userModify);
    }
}
