package com.wellmeet.domain.reservation.repository;

import com.wellmeet.domain.reservation.domain.SelectedPremiumOption;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelectedPremiumOptionRepository extends JpaRepository<SelectedPremiumOption, Long> {

    void deleteAllByReservationId(Long reservationId);

    List<SelectedPremiumOption> findAllByReservationId(Long reservationId);
}
