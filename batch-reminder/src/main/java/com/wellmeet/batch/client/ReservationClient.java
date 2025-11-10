package com.wellmeet.batch.client;

import com.wellmeet.common.dto.ReservationDTO;
import com.wellmeet.common.dto.ReservationStatus;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "domain-reservation-service", path = "/api/reservation")
public interface ReservationClient {

    @GetMapping
    List<ReservationDTO> getReservationsByStatus(@RequestParam("status") ReservationStatus status);
}
