package com.wellmeet.domain.owner;

import com.wellmeet.common.dto.OwnerDTO;
import com.wellmeet.domain.owner.domainservice.OwnerDomainService;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.owner.repository.OwnerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerApplicationService {

    private final OwnerDomainService ownerDomainService;
    private final OwnerRepository ownerRepository;

    @Transactional
    public OwnerDTO createOwner(String name, String email) {
        Owner owner = new Owner(name, email);
        Owner saved = ownerRepository.save(owner);
        return toDTO(saved);
    }

    public OwnerDTO getOwnerById(String ownerId) {
        Owner owner = ownerDomainService.getById(ownerId);
        return toDTO(owner);
    }

    public List<OwnerDTO> getOwnersByIds(List<String> ownerIds) {
        return ownerDomainService.findAllByIds(ownerIds).stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public void deleteOwner(String ownerId) {
        Owner owner = ownerDomainService.getById(ownerId);
        ownerRepository.delete(owner);
    }

    private OwnerDTO toDTO(Owner owner) {
        return new OwnerDTO(
                owner.getId(),
                owner.getName(),
                owner.getEmail(),
                owner.isReservationEnabled(),
                owner.isReviewEnabled()
        );
    }
}
