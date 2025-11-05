package com.wellmeet.client;

import com.wellmeet.client.dto.OwnerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "domain-owner-service")
public interface OwnerClient {

    @GetMapping("/api/owners/{id}")
    OwnerDTO getOwner(@PathVariable("id") String id);
}
