package com.wellmeet.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateCustomerRequest {

    @NotBlank(message = "고객명은 필수입니다.")
    private String name;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
    private String phone;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "생일은 YYYY-MM-DD 형식이어야 합니다.")
    private String birthday;

    private String preferences;
    private String allergies;
    private String notes;
    private List<String> tags;
}
