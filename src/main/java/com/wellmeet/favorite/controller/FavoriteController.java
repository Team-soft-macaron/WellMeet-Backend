package com.wellmeet.favorite.controller;

import com.wellmeet.favorite.dto.FavoriteRestaurantResponse;
import com.wellmeet.favorite.service.FavoriteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping("/api/favorite/restaurants")
    public List<FavoriteRestaurantResponse> getFavoriteRestaurants(
            @RequestParam("memberId") Long memberId
    ) {
        return favoriteService.getFavoriteRestaurants(memberId);
    }

    @PostMapping("/api/favorite/restaurant/{restaurantId}")
    @ResponseStatus(HttpStatus.CREATED)
    public FavoriteRestaurantResponse addFavoriteRestaurant(
            @RequestParam("memberId") Long memberId,
            @PathVariable("restaurantId") Long restaurantId
    ) {
        return favoriteService.addFavoriteRestaurant(memberId, restaurantId);
    }

    @DeleteMapping("/api/favorite/restaurant/{restaurantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavoriteRestaurant(
            @RequestParam("memberId") Long memberId,
            @PathVariable("restaurantId") Long restaurantId
    ) {
        favoriteService.removeFavoriteRestaurant(memberId, restaurantId);
    }
}
