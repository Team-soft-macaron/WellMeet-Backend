package com.wellmeet.restaurant.dto;

import com.wellmeet.client.dto.MenuDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RepresentativeMenuResponse {

    private String name;
    private int price;

    public RepresentativeMenuResponse(MenuDTO menu) {
        this.name = menu.name();
        this.price = menu.price();
    }
}
