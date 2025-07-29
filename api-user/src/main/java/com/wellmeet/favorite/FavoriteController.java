package com.wellmeet.favorite;

import com.wellmeet.favorite.dto.FavoriteRestaurantResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/favorite")
@RestController
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping("/restaurant/list")
    public List<FavoriteRestaurantResponse> getFavoriteRestaurants(
            @RequestParam("memberId") Long memberId // TODO : 로그인 구현 후 ArgumentResolver를 활용하도록 변경
    ) {
        return favoriteService.getFavoriteRestaurants(memberId);
    }

    @PostMapping("/restaurant/{restaurantId}")
    @ResponseStatus(HttpStatus.CREATED)
    public FavoriteRestaurantResponse addFavoriteRestaurant(
            @RequestParam("memberId") Long memberId,
            @PathVariable("restaurantId") String restaurantId
    ) {
        return favoriteService.addFavoriteRestaurant(memberId, restaurantId);
    }

    @DeleteMapping("/restaurant/{restaurantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavoriteRestaurant(
            @RequestParam("memberId") Long memberId,
            @PathVariable("restaurantId") String restaurantId
    ) {
        favoriteService.removeFavoriteRestaurant(memberId, restaurantId);
    }
}
