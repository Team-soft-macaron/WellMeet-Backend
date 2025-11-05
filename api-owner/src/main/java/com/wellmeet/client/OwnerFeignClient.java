package com.wellmeet.client;

import com.wellmeet.common.dto.OwnerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "domain-owner-service")
public interface OwnerFeignClient {

    @GetMapping("/api/owners/{id}")
    OwnerDTO getOwner(@PathVariable("id") String id);
}
