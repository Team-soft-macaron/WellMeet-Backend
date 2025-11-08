package com.wellmeet.domain.favorite;

import com.wellmeet.common.dto.FavoriteRestaurantDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteRestaurantController {

    private final FavoriteRestaurantApplicationService favoriteRestaurantApplicationService;

    @GetMapping("/check")
    public ResponseEntity<Boolean> isFavorite(
            @RequestParam String memberId,
            @RequestParam String restaurantId
    ) {
        boolean isFavorite = favoriteRestaurantApplicationService.isFavorite(memberId, restaurantId);
        return ResponseEntity.ok(isFavorite);
    }

    @GetMapping("/members/{memberId}")
    public ResponseEntity<List<FavoriteRestaurantDTO>> getFavoritesByMemberId(
            @PathVariable String memberId
    ) {
        List<FavoriteRestaurantDTO> responses =
                favoriteRestaurantApplicationService.getFavoritesByMemberId(memberId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<FavoriteRestaurantDTO> addFavorite(
            @RequestParam String memberId,
            @RequestParam String restaurantId
    ) {
        FavoriteRestaurantDTO response =
                favoriteRestaurantApplicationService.addFavorite(memberId, restaurantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> removeFavorite(
            @RequestParam String memberId,
            @RequestParam String restaurantId
    ) {
        favoriteRestaurantApplicationService.removeFavorite(memberId, restaurantId);
        return ResponseEntity.noContent().build();
    }
}
