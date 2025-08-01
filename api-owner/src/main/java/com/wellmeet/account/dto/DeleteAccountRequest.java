package com.wellmeet.account.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeleteAccountRequest {
    
    @NotBlank
    private String password;        // 비밀번호 확인
    
    private String reason;          // 탈퇴 사유
    private String feedback;        // 피드백
}