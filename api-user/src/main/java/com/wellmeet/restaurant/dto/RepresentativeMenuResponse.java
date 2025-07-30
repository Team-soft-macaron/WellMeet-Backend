package com.wellmeet.restaurant.dto;

import com.wellmeet.domain.restaurant.menu.domain.Menu;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RepresentativeMenuResponse {

    private String name;
    private int price;

    public RepresentativeMenuResponse(Menu menu) {
        this.name = menu.getName();
        this.price = menu.getPrice();
    }
}
