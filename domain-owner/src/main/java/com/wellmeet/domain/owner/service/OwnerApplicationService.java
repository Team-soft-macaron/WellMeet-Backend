package com.wellmeet.domain.owner.service;

import com.wellmeet.domain.owner.OwnerDomainService;
import com.wellmeet.domain.owner.dto.OwnerResponse;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.owner.repository.OwnerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OwnerApplicationService {

    private final OwnerDomainService ownerDomainService;
    private final OwnerRepository ownerRepository;

    @Transactional
    public OwnerResponse createOwner(String name, String email) {
        Owner owner = new Owner(name, email);
        Owner saved = ownerRepository.save(owner);
        return OwnerResponse.from(saved);
    }

    public OwnerResponse getOwnerById(String ownerId) {
        Owner owner = ownerDomainService.getById(ownerId);
        return OwnerResponse.from(owner);
    }

    public List<OwnerResponse> getOwnersByIds(List<String> ownerIds) {
        return ownerDomainService.findAllByIds(ownerIds).stream()
                .map(OwnerResponse::from)
                .toList();
    }

    @Transactional
    public void deleteOwner(String ownerId) {
        Owner owner = ownerDomainService.getById(ownerId);
        ownerRepository.delete(owner);
    }
}
