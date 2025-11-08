package com.wellmeet.domain.favorite.entity;

import com.wellmeet.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteRestaurant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "member_id")
    private String memberId;

    @NotBlank
    @Column(name = "restaurant_id")
    private String restaurantId;

    public FavoriteRestaurant(String memberId, String restaurantId) {
        this.memberId = memberId;
        this.restaurantId = restaurantId;
    }
}
