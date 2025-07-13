package com.wellmeet.recommend;

import com.wellmeet.recommend.crawlingreview.domain.VibeName;
import com.wellmeet.recommend.dto.RecommendRestaurantResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/recommend")
@RestController
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping("/restaurant")
    public List<RecommendRestaurantResponse> getRecommendRestaurants(
            @RequestParam(value = "vibe") VibeName vibeName,
            @RequestParam(value = "latitude") double latitude,
            @RequestParam(value = "longitude") double longitude
    ) {
        return recommendService.getRecommendRestaurants(vibeName, latitude, longitude);
    }
}
