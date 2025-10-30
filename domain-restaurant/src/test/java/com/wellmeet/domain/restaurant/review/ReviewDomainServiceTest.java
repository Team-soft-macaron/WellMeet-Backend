package com.wellmeet.domain.restaurant.review;

import static org.assertj.core.api.Assertions.assertThat;

import com.wellmeet.BaseRepositoryTest;

import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.repository.RestaurantRepository;
import com.wellmeet.domain.restaurant.review.entity.Review;
import com.wellmeet.domain.restaurant.review.entity.Situation;
import com.wellmeet.domain.restaurant.review.repository.ReviewRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import(ReviewDomainService.class)
class ReviewDomainServiceTest extends BaseRepositoryTest {

    @Autowired
    private ReviewDomainService reviewDomainService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Nested
    class GetAverageRating {

        @Test
        void 식당의_평균_평점을_조회한다() {
            Restaurant restaurant = createAndSaveRestaurant("test-owner-id");

            createAndSaveReview(restaurant, "member1", 5.0);
            createAndSaveReview(restaurant, "member2", 3.0);

            double averageRating = reviewDomainService.getAverageRating(restaurant.getId());

            assertThat(averageRating).isEqualTo(4.0);
        }

        @Test
        void 리뷰가_없으면_0을_반환한다() {
            Restaurant restaurant = createAndSaveRestaurant("test-owner-id");

            double averageRating = reviewDomainService.getAverageRating(restaurant.getId());

            assertThat(averageRating).isEqualTo(0.0);
        }
    }

    @Nested
    class GetByRestaurantId {

        @Test
        void 식당의_모든_리뷰를_조회한다() {
            Restaurant restaurant = createAndSaveRestaurant("test-owner-id");

            createAndSaveReview(restaurant, "member1", 5.0);
            createAndSaveReview(restaurant, "member2", 4.0);

            List<Review> reviews = reviewDomainService.getByRestaurantId(restaurant.getId());

            assertThat(reviews).hasSize(2);
        }

        @Test
        void 리뷰가_없으면_빈_리스트를_반환한다() {
            Restaurant restaurant = createAndSaveRestaurant("test-owner-id");

            List<Review> reviews = reviewDomainService.getByRestaurantId(restaurant.getId());

            assertThat(reviews).isEmpty();
        }

        @Test
        void 다른_식당의_리뷰는_조회되지_않는다() {
            String ownerId = "test-owner-id";
            Restaurant restaurant1 = createAndSaveRestaurant(ownerId);
            Restaurant restaurant2 = createAndSaveRestaurant(ownerId);

            createAndSaveReview(restaurant1, "member", 5.0);
            createAndSaveReview(restaurant2, "member", 4.0);

            List<Review> reviews = reviewDomainService.getByRestaurantId(restaurant1.getId());

            assertThat(reviews).hasSize(1);
        }
    }

    private Restaurant createAndSaveRestaurant(String ownerId) {
        Restaurant restaurant = new Restaurant(
                UUID.randomUUID().toString(),
                "식당",
                "address",
                37.5,
                127.0,
                "thumbnail",
                ownerId
        );
        return restaurantRepository.save(restaurant);
    }

    private Review createAndSaveReview(Restaurant restaurant, String memberId, double rating) {
        Review review = new Review("맛있어요", rating, Situation.DATE, restaurant, memberId);
        return reviewRepository.save(review);
    }
}
