package com.wellmeet.restaurant.domain.review.domain;

import com.wellmeet.common.domain.BaseEntity;
import com.wellmeet.exception.ErrorCode;
import com.wellmeet.exception.WellMeetException;
import com.wellmeet.member.domain.Member;
import com.wellmeet.restaurant.domain.Restaurant;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    protected static final double MINIMUM_RATING = 0.0;
    protected static final double MAXIMUM_RATING = 5.0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private double rating;

    @Enumerated(value = EnumType.STRING)
    private Situation situation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public Review(String content, double rating, Situation situation, Restaurant restaurant, Member member) {
        if (rating < MINIMUM_RATING || rating > MAXIMUM_RATING) {
            throw new WellMeetException(ErrorCode.INVALID_RATING);
        }

        this.content = content;
        this.rating = rating;
        this.situation = situation;
        this.restaurant = restaurant;
        this.member = member;
    }
}
