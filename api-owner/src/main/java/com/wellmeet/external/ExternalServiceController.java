package com.wellmeet.external;

import com.wellmeet.auth.dto.MessageResponse;
import com.wellmeet.external.dto.IntegrationStatusResponse;
import com.wellmeet.external.dto.IntegrationConfigRequest;
import com.wellmeet.external.dto.IntegrationTestResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/owner/settings/integrations")
@RequiredArgsConstructor
public class ExternalServiceController {

    private final ExternalServiceService externalServiceService;

    @GetMapping
    public IntegrationStatusResponse getIntegrations(@RequestParam Long ownerId) { // TODO: JWT에서 추출
        return externalServiceService.getIntegrationStatus(ownerId);
    }

    @PostMapping("/{service}/connect")
    public MessageResponse connectService(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @PathVariable String service,
            @Valid @RequestBody IntegrationConfigRequest request
    ) {
        return externalServiceService.connectService(ownerId, service, request);
    }

    @DeleteMapping("/{service}/disconnect")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disconnectService(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @PathVariable String service
    ) {
        externalServiceService.disconnectService(ownerId, service);
    }

    @PostMapping("/{service}/test")
    public IntegrationTestResponse testConnection(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @PathVariable String service
    ) {
        return externalServiceService.testConnection(ownerId, service);
    }
}