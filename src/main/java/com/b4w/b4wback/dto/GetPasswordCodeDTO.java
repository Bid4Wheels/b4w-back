package com.b4w.b4wback.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetPasswordCodeDTO {
    private String email;
    private Integer passwordCode;
}
