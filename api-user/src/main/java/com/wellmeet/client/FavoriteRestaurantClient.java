package com.wellmeet.client;

import com.wellmeet.client.dto.FavoriteRestaurantDTO;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "domain-member-service", path = "/api/favorites")
public interface FavoriteRestaurantClient {

    @GetMapping("/check")
    Boolean isFavorite(@RequestParam String memberId, @RequestParam String restaurantId);

    @GetMapping("/members/{memberId}")
    List<FavoriteRestaurantDTO> getFavoritesByMemberId(@PathVariable String memberId);

    @PostMapping
    FavoriteRestaurantDTO addFavorite(@RequestParam String memberId, @RequestParam String restaurantId);

    @DeleteMapping
    void removeFavorite(@RequestParam String memberId, @RequestParam String restaurantId);
}
