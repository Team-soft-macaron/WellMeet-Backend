package com.wellmeet.domain.reservation.entity;

import com.wellmeet.domain.common.BaseEntity;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.entity.Restaurant;
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
    private ReservationStatus status;

    @NotBlank
    private String purpose;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "available_date_id")
    private AvailableDate availableDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private int partySize;
    private String specialRequest;

    public Reservation(String purpose, Restaurant restaurant, AvailableDate availableDate,
                       Member member, int partySize, String specialRequest) {
        this.status = ReservationStatus.PENDING;
        this.purpose = purpose;
        this.restaurant = restaurant;
        this.availableDate = availableDate;
        this.member = member;
        this.partySize = partySize;
        this.specialRequest = specialRequest;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELED;
    }

    public void update(
            AvailableDate availableDate,
            String purpose,
            int partySize,
            String specialRequest
    ) {
        this.availableDate = availableDate;
        this.purpose = purpose;
        this.partySize = partySize;
        this.specialRequest = specialRequest;
    }

    public String getRestaurantName() {
        return restaurant.getName();
    }

    public LocalDateTime getDateTime() {
        return LocalDateTime.of(availableDate.getDate(), availableDate.getTime());
    }
}
