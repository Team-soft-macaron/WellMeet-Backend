package com.wellmeet.client;

import com.wellmeet.client.dto.request.DecreaseCapacityRequest;
import com.wellmeet.client.dto.request.IncreaseCapacityRequest;
import com.wellmeet.common.dto.AvailableDateDTO;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "domain-restaurant-service", contextId = "availableDateClient", path = "/api/available-dates")
public interface RestaurantAvailableDateFeignClient {

    @GetMapping("/restaurant/{restaurantId}")
    List<AvailableDateDTO> getAvailableDatesByRestaurant(@PathVariable String restaurantId);

    @PutMapping("/decrease-capacity")
    void decreaseCapacity(@RequestBody DecreaseCapacityRequest request);

    @PutMapping("/increase-capacity")
    void increaseCapacity(@RequestBody IncreaseCapacityRequest request);
}
