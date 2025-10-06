package com.wellmeet.domain.restaurant.review.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.wellmeet.BaseRepositoryTest;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.member.repository.MemberRepository;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.owner.repository.OwnerRepository;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.repository.RestaurantRepository;
import com.wellmeet.domain.restaurant.review.entity.Review;
import com.wellmeet.domain.restaurant.review.entity.Situation;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReviewRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Nested
    class GetAverageRating {

        @Test
        void 리뷰의_평균_평점을_계산한다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);
            Member member1 = createAndSaveMember("member1");
            Member member2 = createAndSaveMember("member2");
            Member member3 = createAndSaveMember("member3");

            createAndSaveReview(restaurant, member1, 5.0);
            createAndSaveReview(restaurant, member2, 4.0);
            createAndSaveReview(restaurant, member3, 3.0);

            double averageRating = reviewRepository.getAverageRating(restaurant.getId());

            assertThat(averageRating).isEqualTo(4.0);
        }

        @Test
        void 리뷰가_없으면_0을_반환한다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);

            double averageRating = reviewRepository.getAverageRating(restaurant.getId());

            assertThat(averageRating).isEqualTo(0.0);
        }

        @Test
        void 소수점_평균_평점을_정확하게_계산한다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);
            Member member1 = createAndSaveMember("member1");
            Member member2 = createAndSaveMember("member2");

            createAndSaveReview(restaurant, member1, 4.5);
            createAndSaveReview(restaurant, member2, 3.5);

            double averageRating = reviewRepository.getAverageRating(restaurant.getId());

            assertThat(averageRating).isEqualTo(4.0);
        }

        @Test
        void 다른_식당의_리뷰는_평균_계산에_포함되지_않는다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant1 = createAndSaveRestaurant(owner);
            Restaurant restaurant2 = createAndSaveRestaurant(owner);
            Member member = createAndSaveMember("member");

            createAndSaveReview(restaurant1, member, 5.0);
            createAndSaveReview(restaurant2, member, 1.0);

            double averageRating = reviewRepository.getAverageRating(restaurant1.getId());

            assertThat(averageRating).isEqualTo(5.0);
        }
    }

    private Owner createAndSaveOwner() {
        Owner owner = new Owner("owner", "owner@test.com");
        return ownerRepository.save(owner);
    }

    private Restaurant createAndSaveRestaurant(Owner owner) {
        Restaurant restaurant = new Restaurant(
                UUID.randomUUID().toString(),
                "식당",
                "address",
                37.5,
                127.0,
                "thumbnail",
                owner
        );
        return restaurantRepository.save(restaurant);
    }

    private Member createAndSaveMember(String name) {
        Member member = new Member(name, "nickname", name + "@test.com", "010-1234-5678");
        return memberRepository.save(member);
    }

    private Review createAndSaveReview(Restaurant restaurant, Member member, double rating) {
        Review review = new Review("맛있어요", rating, Situation.DATE, restaurant, member);
        return reviewRepository.save(review);
    }
}
