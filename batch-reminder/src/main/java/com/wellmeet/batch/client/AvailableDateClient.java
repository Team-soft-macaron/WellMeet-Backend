package com.wellmeet.batch.client;

import com.wellmeet.common.dto.AvailableDateDTO;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "domain-restaurant-service", path = "/api/available-dates")
public interface AvailableDateClient {

    @PostMapping("/batch")
    List<AvailableDateDTO> getAvailableDatesByIds(@RequestBody AvailableDateIdsRequest request);

    record AvailableDateIdsRequest(List<Long> availableDateIds) {
    }
}
