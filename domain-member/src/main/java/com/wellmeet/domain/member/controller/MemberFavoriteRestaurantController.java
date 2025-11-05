package com.wellmeet.domain.member.controller;

import com.wellmeet.domain.member.dto.FavoriteRestaurantResponse;
import com.wellmeet.domain.member.service.MemberFavoriteRestaurantApplicationService;
import java.util.List;
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
public class MemberFavoriteRestaurantController {

    private final MemberFavoriteRestaurantApplicationService favoriteRestaurantApplicationService;

    public MemberFavoriteRestaurantController(MemberFavoriteRestaurantApplicationService favoriteRestaurantApplicationService) {
        this.favoriteRestaurantApplicationService = favoriteRestaurantApplicationService;
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> isFavorite(
            @RequestParam String memberId,
            @RequestParam String restaurantId
    ) {
        boolean isFavorite = favoriteRestaurantApplicationService.isFavorite(memberId, restaurantId);
        return ResponseEntity.ok(isFavorite);
    }

    @GetMapping("/members/{memberId}")
    public ResponseEntity<List<FavoriteRestaurantResponse>> getFavoritesByMemberId(
            @PathVariable String memberId
    ) {
        List<FavoriteRestaurantResponse> responses =
                favoriteRestaurantApplicationService.getFavoritesByMemberId(memberId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<FavoriteRestaurantResponse> addFavorite(
            @RequestParam String memberId,
            @RequestParam String restaurantId
    ) {
        FavoriteRestaurantResponse response =
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
