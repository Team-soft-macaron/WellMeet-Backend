package com.wellmeet.fixture;

import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.owner.repository.OwnerRepository;
import org.springframework.stereotype.Component;

@Component
public class OwnerGenerator {

    private final OwnerRepository ownerRepository;

    public OwnerGenerator(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    public Owner generate(String name) {
        Owner owner = new Owner(name, name + "@example.com");
        return ownerRepository.save(owner);
    }
}