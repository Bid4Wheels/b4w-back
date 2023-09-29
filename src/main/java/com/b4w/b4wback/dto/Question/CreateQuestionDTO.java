package com.b4w.b4wback.dto.Question;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuestionDTO {
    @Size(max = 400, min = 10)
    private String question;
    @NotNull
    private Long auctionId;
}
