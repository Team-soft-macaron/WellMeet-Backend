package com.wellmeet.domain.owner.domainservice;

import com.wellmeet.domain.exception.OwnerErrorCode;
import com.wellmeet.domain.exception.OwnerException;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.owner.repository.OwnerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnerDomainService {

    private final OwnerRepository ownerRepository;

    public Owner save(Owner owner) {
        return ownerRepository.save(owner);
    }

    public Owner getById(String ownerId) {
        return ownerRepository.findById(ownerId)
                .orElseThrow(() -> new OwnerException(OwnerErrorCode.OWNER_NOT_FOUND));
    }

    public List<Owner> findAllByIds(List<String> ownerIds) {
        return ownerRepository.findAllById(ownerIds);
    }

    public void delete(Owner owner) {
        ownerRepository.delete(owner);
    }
}
