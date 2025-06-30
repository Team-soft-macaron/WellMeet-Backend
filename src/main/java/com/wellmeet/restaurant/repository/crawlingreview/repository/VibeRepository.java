package com.wellmeet.restaurant.repository.crawlingreview.repository;

import com.wellmeet.restaurant.domain.crawlingreview.domain.Vibe;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VibeRepository extends JpaRepository<Vibe, Long> {

    Optional<Vibe> findByName(String name);
}
