package com.wellmeet.domain.owner.entity;

import com.wellmeet.domain.common.BaseEntity;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Owner extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String email;

    private String password;

    private boolean reservationEnabled;
    private boolean reviewEnabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    public Owner(String name, String email, Restaurant restaurant) {
        this.name = name;
        this.email = email;
        this.reservationEnabled = true;
        this.reviewEnabled = true;
        this.restaurant = restaurant;
    }

    public Owner(String name, String email, String password, Restaurant restaurant) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.reservationEnabled = true;
        this.reviewEnabled = true;
        this.restaurant = restaurant;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
