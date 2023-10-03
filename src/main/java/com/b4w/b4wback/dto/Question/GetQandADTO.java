package com.b4w.b4wback.dto.Question;

import com.b4w.b4wback.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetQandADTO {
    private String question;
    private String answer;
    private UserDTO user;
}
