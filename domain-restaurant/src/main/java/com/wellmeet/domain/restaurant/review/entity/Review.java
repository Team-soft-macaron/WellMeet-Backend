package com.wellmeet.domain.restaurant.review.entity;

import com.wellmeet.domain.common.BaseEntity;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.exception.RestaurantErrorCode;
import com.wellmeet.domain.restaurant.exception.RestaurantException;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    protected static final double MINIMUM_RATING = 0.0;
    protected static final double MAXIMUM_RATING = 5.0;
    protected static final int MAX_CONTENT_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String content;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Situation situation;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @NotBlank
    @Column(name = "member_id")
    private String memberId;

    private double rating;

    public Review(String content, double rating, Situation situation, Restaurant restaurant, String memberId) {
        validateContent(content);
        validateRating(rating);

        this.content = content;
        this.rating = rating;
        this.situation = situation;
        this.restaurant = restaurant;
        this.memberId = memberId;
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank() || content.length() > MAX_CONTENT_LENGTH) {
            throw new RestaurantException(RestaurantErrorCode.INVALID_REVIEW_CONTENT);
        }
    }

    private void validateRating(double rating) {
        if (rating < MINIMUM_RATING || rating > MAXIMUM_RATING) {
            throw new RestaurantException(RestaurantErrorCode.INVALID_RATING);
        }
    }
}
