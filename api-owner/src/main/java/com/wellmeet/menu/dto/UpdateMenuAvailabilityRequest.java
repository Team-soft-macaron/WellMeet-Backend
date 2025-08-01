package com.wellmeet.menu.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateMenuAvailabilityRequest {
    
    @NotNull(message = "판매 가능 여부는 필수입니다.")
    private Boolean available;
}