package com.wellmeet.restaurant.availabledate.domain;

import com.wellmeet.restaurant.domain.Restaurant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AvailableDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "available_date")
    private LocalDate date;

    @NotNull
    @Column(name = "available_time")
    private LocalTime time;

    private int maxCapacity;
    private boolean isAvailable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    public AvailableDate(LocalDate date, LocalTime time, int maxCapacity, Restaurant restaurant) {
        this.date = date;
        this.time = time;
        this.maxCapacity = maxCapacity;
        this.isAvailable = true;
        this.restaurant = restaurant;
    }
}
