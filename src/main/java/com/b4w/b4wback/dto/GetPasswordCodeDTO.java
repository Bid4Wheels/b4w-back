package com.b4w.b4wback.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPasswordCodeDTO {
    private String email;
    private Integer passwordCode;
    private boolean matchingCode;
}
