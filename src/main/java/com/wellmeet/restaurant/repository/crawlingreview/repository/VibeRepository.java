package com.wellmeet.restaurant.repository.crawlingreview.repository;

import com.wellmeet.restaurant.domain.crawlingreview.domain.Vibe;
import com.wellmeet.restaurant.domain.crawlingreview.domain.VibeName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VibeRepository extends JpaRepository<Vibe, Long> {

    Optional<Vibe> findByName(VibeName name);
}
