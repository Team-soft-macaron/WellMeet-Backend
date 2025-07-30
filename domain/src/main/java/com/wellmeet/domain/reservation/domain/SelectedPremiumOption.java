package com.wellmeet.domain.reservation.domain;

import com.wellmeet.domain.restaurant.domain.PremiumOption;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelectedPremiumOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "premium_option_id")
    private PremiumOption premiumOption;

    public SelectedPremiumOption(Reservation reservation, PremiumOption premiumOption) {
        this.reservation = reservation;
        this.premiumOption = premiumOption;
    }

    public String getName() {
        return premiumOption.getName();
    }
}
