package com.wellmeet.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateReplyRequest {
    
    @NotBlank(message = "답글 내용은 필수입니다.")
    @Size(max = 500, message = "답글은 최대 500자까지 입력 가능합니다.")
    private String reply;
}