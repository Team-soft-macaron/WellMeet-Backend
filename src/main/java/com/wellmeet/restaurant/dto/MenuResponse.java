package com.wellmeet.restaurant.dto;

import com.wellmeet.restaurant.domain.menu.domain.Menu;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MenuResponse {

    private String name;
    private int price;

    public MenuResponse(Menu menu) {
        this.name = menu.getName();
        this.price = menu.getPrice();
    }
}
