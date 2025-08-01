package com.wellmeet.menu.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateMenuRequest {

    @NotBlank(message = "카테고리는 필수입니다.")
    private String category;

    @NotBlank(message = "메뉴명은 필수입니다.")
    private String name;

    private String description;

    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Integer price;

    private String image;
    private List<String> allergens;
    private Integer spicyLevel;
}
