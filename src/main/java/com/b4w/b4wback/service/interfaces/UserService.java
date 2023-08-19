package com.b4w.b4wback.service.interfaces;

import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.model.User;

public interface UserService {
    /**
     * Given a user dto it returns a user entity.
     * The user checks for multiple non null.
     * The user checks for unique email.
     * @param createUserDTO a valid user dto
     * @return a user entity with the data of user
     */
    User createUser(CreateUserDTO createUserDTO);
}
