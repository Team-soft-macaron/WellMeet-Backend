package com.wellmeet.restaurant.domain.crawlingreview.domain;

import com.wellmeet.common.domain.BaseEntity;
import com.wellmeet.restaurant.domain.Restaurant;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrawlingReview extends BaseEntity {

    @Id
    private String id;
    private String content;
    private double rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    public CrawlingReview(String id, String content, double rating, Restaurant restaurant) {
        this.id = id;
        this.content = content;
        this.rating = rating;
        this.restaurant = restaurant;
    }
}
