package com.wellmeet.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "현재 비밀번호는 필수입니다.")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호는 필수입니다.")
    @Size(min = 8, max = 128, message = "비밀번호는 8-128자 사이여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", 
             message = "비밀번호는 대소문자, 숫자, 특수문자를 포함해야 합니다.")
    private String newPassword;

    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String confirmPassword;
}