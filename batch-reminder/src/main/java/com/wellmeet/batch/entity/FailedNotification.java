package com.wellmeet.batch.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "failed_notification")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FailedNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long reservationId;

    @NotNull
    private String customerId;

    @NotNull
    private String customerName;

    @NotNull
    private String restaurantName;

    @NotNull
    private LocalDateTime reservationTime;

    @NotNull
    private Integer partySize;

    private String failureReason;

    @NotNull
    private LocalDateTime failureTime;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private FailedNotificationStatus status;

    public FailedNotification(Long reservationId, String customerId, String customerName,
                              String restaurantName, LocalDateTime reservationTime,
                              Integer partySize, String failureReason, LocalDateTime failureTime) {
        this.reservationId = reservationId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.restaurantName = restaurantName;
        this.reservationTime = reservationTime;
        this.partySize = partySize;
        this.failureReason = failureReason;
        this.failureTime = failureTime;
        this.status = FailedNotificationStatus.PENDING;
    }
}
