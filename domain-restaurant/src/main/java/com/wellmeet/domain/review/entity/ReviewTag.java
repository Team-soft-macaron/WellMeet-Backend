package com.wellmeet.domain.review.entity;

import com.wellmeet.domain.common.BaseEntity;
import com.wellmeet.domain.exception.RestaurantErrorCode;
import com.wellmeet.domain.exception.RestaurantException;
import jakarta.persistence.Entity;
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
public class ReviewTag extends BaseEntity {

    protected static final int MAX_NAME_LENGTH = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @NotBlank
    private String name;

    public ReviewTag(Review review, String name) {
        validateName(name);

        this.review = review;
        this.name = name;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank() || name.length() > MAX_NAME_LENGTH) {
            throw new RestaurantException(RestaurantErrorCode.INVALID_REVIEW_TAG_NAME);
        }
    }
}
