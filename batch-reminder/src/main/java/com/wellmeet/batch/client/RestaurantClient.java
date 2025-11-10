package com.wellmeet.batch.client;

import com.wellmeet.common.dto.RestaurantDTO;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "domain-restaurant-service", path = "/api/restaurants")
public interface RestaurantClient {

    @GetMapping("/{id}")
    RestaurantDTO getRestaurantById(@PathVariable("id") String id);

    @PostMapping("/batch")
    List<RestaurantDTO> getRestaurantsByIds(@RequestBody RestaurantIdsRequest request);

    record RestaurantIdsRequest(List<String> restaurantIds) {
    }
}
