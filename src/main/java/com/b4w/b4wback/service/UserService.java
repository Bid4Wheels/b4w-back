package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.CreateAppUserDTO;


public interface UserService {
    CreateAppUserDTO getUserById(long id);

    CreateAppUserDTO createUser(CreateAppUserDTO user);
}
