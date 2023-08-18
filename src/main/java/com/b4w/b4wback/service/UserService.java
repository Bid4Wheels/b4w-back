package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.CreateUserDTO;


public interface UserService {
    CreateUserDTO getUserById(long id);

    CreateUserDTO createUser(CreateUserDTO user);
}
