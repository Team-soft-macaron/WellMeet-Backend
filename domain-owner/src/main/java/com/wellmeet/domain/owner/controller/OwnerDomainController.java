package com.wellmeet.domain.owner.controller;

import com.wellmeet.common.dto.OwnerDTO;
import com.wellmeet.domain.owner.dto.CreateOwnerRequest;
import com.wellmeet.domain.owner.dto.OwnerIdsRequest;
import com.wellmeet.domain.owner.service.OwnerApplicationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/owners")
public class OwnerDomainController {

    private final OwnerApplicationService ownerApplicationService;

    public OwnerDomainController(OwnerApplicationService ownerApplicationService) {
        this.ownerApplicationService = ownerApplicationService;
    }

    @PostMapping
    public ResponseEntity<OwnerDTO> createOwner(@Valid @RequestBody CreateOwnerRequest request) {
        OwnerDTO response = ownerApplicationService.createOwner(
                request.name(),
                request.email()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OwnerDTO> getOwner(@PathVariable String id) {
        OwnerDTO response = ownerApplicationService.getOwnerById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<OwnerDTO>> getOwnersByIds(
            @Valid @RequestBody OwnerIdsRequest request
    ) {
        List<OwnerDTO> responses = ownerApplicationService.getOwnersByIds(request.ownerIds());
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOwner(@PathVariable String id) {
        ownerApplicationService.deleteOwner(id);
        return ResponseEntity.noContent().build();
    }
}
