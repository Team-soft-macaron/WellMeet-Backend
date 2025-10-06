package com.wellmeet.domain.restaurant.review;

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

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Nested
    class GetAverageRating {

        @Test
        void 식당의_평균_평점을_조회한다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);
            Member member1 = createAndSaveMember("member1");
            Member member2 = createAndSaveMember("member2");

            createAndSaveReview(restaurant, member1, 5.0);
            createAndSaveReview(restaurant, member2, 3.0);

            double averageRating = reviewDomainService.getAverageRating(restaurant.getId());

            assertThat(averageRating).isEqualTo(4.0);
        }

        @Test
        void 리뷰가_없으면_0을_반환한다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);

            double averageRating = reviewDomainService.getAverageRating(restaurant.getId());

            assertThat(averageRating).isEqualTo(0.0);
        }
    }

    @Nested
    class GetByRestaurantId {

        @Test
        void 식당의_모든_리뷰를_조회한다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);
            Member member1 = createAndSaveMember("member1");
            Member member2 = createAndSaveMember("member2");

            createAndSaveReview(restaurant, member1, 5.0);
            createAndSaveReview(restaurant, member2, 4.0);

            List<Review> reviews = reviewDomainService.getByRestaurantId(restaurant.getId());

            assertThat(reviews).hasSize(2);
        }

        @Test
        void 리뷰가_없으면_빈_리스트를_반환한다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);

            List<Review> reviews = reviewDomainService.getByRestaurantId(restaurant.getId());

            assertThat(reviews).isEmpty();
        }

        @Test
        void 다른_식당의_리뷰는_조회되지_않는다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant1 = createAndSaveRestaurant(owner);
            Restaurant restaurant2 = createAndSaveRestaurant(owner);
            Member member = createAndSaveMember("member");

            createAndSaveReview(restaurant1, member, 5.0);
            createAndSaveReview(restaurant2, member, 4.0);

            List<Review> reviews = reviewDomainService.getByRestaurantId(restaurant1.getId());

            assertThat(reviews).hasSize(1);
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
