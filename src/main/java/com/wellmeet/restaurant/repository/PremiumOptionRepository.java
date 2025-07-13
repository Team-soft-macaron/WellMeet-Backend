package com.wellmeet.restaurant.repository;

import com.wellmeet.restaurant.domain.PremiumOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PremiumOptionRepository extends JpaRepository<PremiumOption, Long> {
}
