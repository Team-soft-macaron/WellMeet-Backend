package com.wellmeet.domain.owner;

import com.wellmeet.domain.exception.DomainErrorCode;
import com.wellmeet.domain.exception.WellMeetDomainException;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.owner.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnerDomainService {
    
    private final OwnerRepository ownerRepository;
    
    public Owner getById(Long ownerId) {
        return ownerRepository.findById(ownerId)
                .orElseThrow(() -> new WellMeetDomainException(DomainErrorCode.OWNER_NOT_FOUND));
    }

    public Owner save(Owner owner) {
        return ownerRepository.save(owner);
    }
}
