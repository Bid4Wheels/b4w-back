package com.b4w.b4wback.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetPasswordCodeDTO {
    private String email;
    private Integer passwordCode;
}
