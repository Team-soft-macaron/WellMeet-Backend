package com.wellmeet.domain.member;

import static org.assertj.core.api.Assertions.*;

import com.wellmeet.BaseRepositoryTest;
import com.wellmeet.domain.member.entity.FavoriteRestaurant;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.member.exception.MemberException;
import com.wellmeet.domain.member.repository.FavoriteRestaurantRepository;
import com.wellmeet.domain.member.repository.MemberRepository;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.owner.repository.OwnerRepository;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.repository.RestaurantRepository;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import(FavoriteRestaurantDomainService.class)
class FavoriteRestaurantDomainServiceTest extends BaseRepositoryTest {

    @Autowired
    private FavoriteRestaurantDomainService favoriteRestaurantDomainService;

    @Autowired
    private FavoriteRestaurantRepository favoriteRestaurantRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Nested
    class IsFavorite {

        @Test
        void 즐겨찾기에_등록된_레스토랑이면_true를_반환한다() {
            Member member = createAndSaveMember("member");
            Restaurant restaurant = createAndSaveRestaurant("restaurant");
            favoriteRestaurantRepository.save(new FavoriteRestaurant(member, restaurant));

            boolean result = favoriteRestaurantDomainService.isFavorite(member.getId(), restaurant.getId());

            assertThat(result).isTrue();
        }

        @Test
        void 즐겨찾기에_등록되지_않은_레스토랑이면_false를_반환한다() {
            Member member = createAndSaveMember("member");
            Restaurant restaurant = createAndSaveRestaurant("restaurant");

            boolean result = favoriteRestaurantDomainService.isFavorite(member.getId(), restaurant.getId());

            assertThat(result).isFalse();
        }
    }

    @Nested
    class FindAllByMemberId {

        @Test
        void 회원의_모든_즐겨찾기_레스토랑을_조회한다() {
            Member member = createAndSaveMember("member");
            Restaurant restaurant1 = createAndSaveRestaurant("restaurant1");
            Restaurant restaurant2 = createAndSaveRestaurant("restaurant2");
            favoriteRestaurantRepository.save(new FavoriteRestaurant(member, restaurant1));
            favoriteRestaurantRepository.save(new FavoriteRestaurant(member, restaurant2));

            List<FavoriteRestaurant> result = favoriteRestaurantDomainService.findAllByMemberId(member.getId());

            assertThat(result).hasSize(2);
        }

        @Test
        void 즐겨찾기가_없으면_빈_리스트를_반환한다() {
            Member member = createAndSaveMember("member");

            List<FavoriteRestaurant> result = favoriteRestaurantDomainService.findAllByMemberId(member.getId());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class Save {

        @Test
        void 즐겨찾기_레스토랑을_저장한다() {
            Member member = createAndSaveMember("member");
            Restaurant restaurant = createAndSaveRestaurant("restaurant");
            FavoriteRestaurant favoriteRestaurant = new FavoriteRestaurant(member, restaurant);

            favoriteRestaurantDomainService.save(favoriteRestaurant);

            FavoriteRestaurant saved = favoriteRestaurantRepository.findByMemberIdAndRestaurantId(
                    member.getId(), restaurant.getId()
            ).orElseThrow();
            assertThat(saved.getMember().getId()).isEqualTo(member.getId());
            assertThat(saved.getRestaurant().getId()).isEqualTo(restaurant.getId());
        }
    }

    @Nested
    class GetByMemberIdAndRestaurantId {

        @Test
        void 즐겨찾기_레스토랑을_조회한다() {
            Member member = createAndSaveMember("member");
            Restaurant restaurant = createAndSaveRestaurant("restaurant");
            favoriteRestaurantRepository.save(new FavoriteRestaurant(member, restaurant));

            FavoriteRestaurant result = favoriteRestaurantDomainService.getByMemberIdAndRestaurantId(
                    member.getId(), restaurant.getId()
            );

            assertThat(result.getMember().getId()).isEqualTo(member.getId());
            assertThat(result.getRestaurant().getId()).isEqualTo(restaurant.getId());
        }

        @Test
        void 존재하지_않으면_예외가_발생한다() {
            Member member = createAndSaveMember("member");
            Restaurant restaurant = createAndSaveRestaurant("restaurant");

            assertThatThrownBy(() ->
                    favoriteRestaurantDomainService.getByMemberIdAndRestaurantId(
                            member.getId(), restaurant.getId()
                    )
            ).isInstanceOf(MemberException.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void 즐겨찾기_레스토랑을_삭제한다() {
            Member member = createAndSaveMember("member");
            Restaurant restaurant = createAndSaveRestaurant("restaurant");
            FavoriteRestaurant favoriteRestaurant = favoriteRestaurantRepository.save(
                    new FavoriteRestaurant(member, restaurant)
            );

            favoriteRestaurantDomainService.delete(favoriteRestaurant);

            boolean exists = favoriteRestaurantRepository.existsByMemberIdAndRestaurantId(
                    member.getId(), restaurant.getId()
            );
            assertThat(exists).isFalse();
        }
    }

    private Member createAndSaveMember(String name) {
        Member member = new Member(name, "nickname", "email@example.com", "010-1234-5678");
        return memberRepository.save(member);
    }

    private Restaurant createAndSaveRestaurant(String name) {
        Owner owner = ownerRepository.save(new Owner("owner", "owner@example.com"));
        Restaurant restaurant = new Restaurant(name, "description", "address", 37.5, 127.0, "thumbnail", owner);
        return restaurantRepository.save(restaurant);
    }
}
