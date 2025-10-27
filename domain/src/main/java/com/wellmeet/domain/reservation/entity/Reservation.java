package com.wellmeet.domain.reservation.entity;

import com.wellmeet.domain.common.BaseEntity;
import com.wellmeet.domain.reservation.exception.ReservationErrorCode;
import com.wellmeet.domain.reservation.exception.ReservationException;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(
        name = "reservation",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_member_restaurant_available_date",
                columnNames = {"member_id", "restaurant_id", "available_date_id"}
        )
)
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    protected static final int MAX_REQUEST_LENGTH = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private ReservationStatus status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "available_date_id")
    private AvailableDate availableDate;

    @NotBlank
    @Column(name = "member_id")
    private String memberId;

    private int partySize;
    private String specialRequest;

    public Reservation(Restaurant restaurant, AvailableDate availableDate,
                       String memberId, int partySize, String specialRequest) {
        validatePartySize(partySize);
        validateRequest(specialRequest);

        this.status = ReservationStatus.PENDING;
        this.restaurant = restaurant;
        this.availableDate = availableDate;
        this.memberId = memberId;
        this.partySize = partySize;
        this.specialRequest = specialRequest;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELED;
    }

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }

    public void update(
            AvailableDate availableDate,
            int partySize,
            String specialRequest
    ) {
        validatePartySize(partySize);
        validateRequest(specialRequest);

        this.availableDate = availableDate;
        this.partySize = partySize;
        this.specialRequest = specialRequest;
    }

    private void validatePartySize(int partySize) {
        if (partySize <= 0) {
            throw new ReservationException(ReservationErrorCode.PARTY_SIZE_INVALID);
        }
    }

    private void validateRequest(String specialRequest) {
        if (specialRequest != null && specialRequest.length() > MAX_REQUEST_LENGTH) {
            throw new ReservationException(ReservationErrorCode.REQUEST_INVALID);
        }
    }

    public String getRestaurantName() {
        return restaurant.getName();
    }

    public LocalDateTime getDateTime() {
        return LocalDateTime.of(availableDate.getDate(), availableDate.getTime());
    }
}
