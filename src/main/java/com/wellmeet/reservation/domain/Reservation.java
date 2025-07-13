package com.wellmeet.reservation.domain;

import com.wellmeet.common.domain.BaseEntity;
import com.wellmeet.restaurant.domain.Restaurant;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDateTime dateTime;

    @NotNull
    private ReservationStatus status;

    @NotBlank
    private String purpose;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @NotNull
    private Long memberId;

    private int partySize;
    private String specialRequest;

    public Reservation(LocalDateTime dateTime, String purpose,
                       Restaurant restaurant, Long memberId, int partySize, String specialRequest) {
        this.dateTime = dateTime;
        this.status = ReservationStatus.PENDING;
        this.purpose = purpose;
        this.restaurant = restaurant;
        this.memberId = memberId;
        this.partySize = partySize;
        this.specialRequest = specialRequest;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELED;
    }

    public void update(
            LocalDateTime dateTime,
            String purpose,
            int partySize,
            String specialRequest
    ) {
        this.dateTime = dateTime;
        this.purpose = purpose;
        this.partySize = partySize;
        this.specialRequest = specialRequest;
    }

    public String getRestaurantName() {
        return restaurant.getName();
    }
}
