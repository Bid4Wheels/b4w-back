package com.b4w.b4wback.service.interfaces;

import com.b4w.b4wback.dto.*;
import com.b4w.b4wback.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
    /**
     * Given a user dto it returns a user entity.
     * The user checks for multiple non null.
     * The user checks for unique email.
     * @param createUserDTO a valid user dto
     * @return a user entity with the data of user
     */
    User createUser(CreateUserDTO createUserDTO);

    UserDTO getUserById(Long id);

    UserDetailsService userDetailsService();

    void getPasswordChangerForId(long id, PasswordChangerDTO userDTO);

    Boolean getPasswordCode(GetPasswordCodeDTO email);

    void changePassword(ChangePasswordDTO passwordChangerDTO);
}
