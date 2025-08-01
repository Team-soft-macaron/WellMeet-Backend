package com.wellmeet.menu.dto;

import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateMenuRequest {

    private String name;
    private String description;

    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Integer price;

    private String category;
    private String image;
    private List<String> allergens;
    private Integer spicyLevel;
    private Boolean isPopular;
    private Boolean isNew;
    private Integer order;
}
