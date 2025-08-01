package com.wellmeet.customer.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateVipStatusRequest {

    @NotNull(message = "VIP 상태는 필수입니다.")
    private Boolean isVip;
}