package com.b4w.b4wback.service.impl;

import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service

public class UserServiceImpl implements UserService {

    private final UserRepository UserRepository;

    public UserServiceImpl(UserRepository userRepository) {
        UserRepository = userRepository;
    }

    @Override
    public CreateUserDTO getUserById(long id) {
        Optional<User> user = UserRepository.findById(id);
        if (user.isPresent()) {
            return CreateUserDTO.builder().name(user.get().getName()).lastName(user.get().getLastName()).email(user.get().getEmail()).phoneNumber(user.get().getPhoneNumber()).password(user.get().getPassword()).build();
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public CreateUserDTO createUser(CreateUserDTO user) {
        User newUser = User.builder().name(user.getName()).lastName(user.getLastName()).email(user.getEmail()).phoneNumber(user.getPhoneNumber()).password(user.getPassword()).build();
        return UserRepository.save(newUser).toDTO();
    }
}
