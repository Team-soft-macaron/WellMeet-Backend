package com.wellmeet.recommend.crawlingreview.repository;

import com.wellmeet.recommend.crawlingreview.domain.Vibe;
import com.wellmeet.recommend.crawlingreview.domain.VibeName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VibeRepository extends JpaRepository<Vibe, Long> {

    Optional<Vibe> findByName(VibeName name);
}
