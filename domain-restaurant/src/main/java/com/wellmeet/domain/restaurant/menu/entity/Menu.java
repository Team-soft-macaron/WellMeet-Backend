package com.wellmeet.domain.restaurant.menu.entity;

import com.wellmeet.domain.common.BaseEntity;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.exception.RestaurantErrorCode;
import com.wellmeet.domain.restaurant.exception.RestaurantException;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private String description;
    private int price;

    public Menu(String name, String description, int price, Restaurant restaurant) {
        validatePrice(price);

        this.name = name;
        this.description = description;
        this.price = price;
        this.restaurant = restaurant;
    }

    private void validatePrice(int price) {
        if (price < 0) {
            throw new RestaurantException(RestaurantErrorCode.INVALID_MENU_PRICE);
        }
    }
}
