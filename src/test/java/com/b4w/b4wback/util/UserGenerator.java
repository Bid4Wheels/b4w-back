package com.b4w.b4wback.util;

import com.b4w.b4wback.dto.CreateUserDTO;

import java.util.ArrayList;
import java.util.List;

public class UserGenerator {

    public static List<CreateUserDTO> generateUsers() {
        List<CreateUserDTO> users = new ArrayList<>();
        users.add(new CreateUserDTO("John", "Doe", "john@example.com", 123456789, "password123"));
        users.add(new CreateUserDTO("Jane", "Smith", "jane@example.com", 987654321, "securepass"));
        users.add((new CreateUserDTO("Michael", "Johnson", "michael@example.com", 555555555, "mysecretpwd")));
        users.add(new CreateUserDTO("Emily", "Brown", "emily@example.com", 111222333, "testpass123"));
        users.add(new CreateUserDTO("William", "Davis", "william@example.com", 444333222, "samplepass"));
        users.add(new CreateUserDTO("Olivia", "Martinez", "olivia@example.com", 999888777, "userpass"));
        users.add(new CreateUserDTO("James", "Jones", "james@example.com", 777666555,"123456"));
        return users;
    }

}
