package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.model.User;

import java.util.Optional;

public interface UserService {
    CreateUserDTO createUser(CreateUserDTO createUserDTO);
    Optional<User> getUserById (Long id);
}
