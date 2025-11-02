package com.wellmeet.domain.owner;

import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.owner.exception.OwnerErrorCode;
import com.wellmeet.domain.owner.exception.OwnerException;
import com.wellmeet.domain.owner.repository.OwnerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OwnerDomainService {

    private final OwnerRepository ownerRepository;

    public Owner getById(String ownerId) {
        return ownerRepository.findById(ownerId)
                .orElseThrow(() -> new OwnerException(OwnerErrorCode.OWNER_NOT_FOUND));
    }

    public List<Owner> findAllByIds(List<String> ownerIds) {
        return ownerRepository.findAllById(ownerIds);
    }
}
