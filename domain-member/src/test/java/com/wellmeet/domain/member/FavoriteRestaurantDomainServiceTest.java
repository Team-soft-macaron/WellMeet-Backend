package com.wellmeet.domain.member;

import static org.assertj.core.api.Assertions.*;

import com.wellmeet.BaseRepositoryTest;
import com.wellmeet.domain.favorite.domainservice.FavoriteRestaurantDomainService;
import com.wellmeet.domain.favorite.entity.FavoriteRestaurant;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.exception.MemberException;
import com.wellmeet.domain.favorite.repository.FavoriteRestaurantRepository;
import com.wellmeet.domain.member.repository.MemberRepository;
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

    @Nested
    class IsFavorite {

        @Test
        void 즐겨찾기에_등록된_레스토랑이면_true를_반환한다() {
            Member member = createAndSaveMember("member");
            String restaurantId = "restaurant-id";
            favoriteRestaurantRepository.save(new FavoriteRestaurant(member.getId(), restaurantId));

            boolean result = favoriteRestaurantDomainService.isFavorite(member.getId(), restaurantId);

            assertThat(result).isTrue();
        }

        @Test
        void 즐겨찾기에_등록되지_않은_레스토랑이면_false를_반환한다() {
            Member member = createAndSaveMember("member");
            String restaurantId = "restaurant-id";

            boolean result = favoriteRestaurantDomainService.isFavorite(member.getId(), restaurantId);

            assertThat(result).isFalse();
        }
    }

    @Nested
    class FindAllByMemberId {

        @Test
        void 회원의_모든_즐겨찾기_레스토랑을_조회한다() {
            Member member = createAndSaveMember("member");
            String restaurantId1 = "restaurant-id-1";
            String restaurantId2 = "restaurant-id-2";
            favoriteRestaurantRepository.save(new FavoriteRestaurant(member.getId(), restaurantId1));
            favoriteRestaurantRepository.save(new FavoriteRestaurant(member.getId(), restaurantId2));

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
            String restaurantId = "restaurant-id";
            FavoriteRestaurant favoriteRestaurant = new FavoriteRestaurant(member.getId(), restaurantId);

            favoriteRestaurantDomainService.save(favoriteRestaurant);

            FavoriteRestaurant saved = favoriteRestaurantRepository.findByMemberIdAndRestaurantId(
                    member.getId(), restaurantId
            ).orElseThrow();
            assertThat(saved.getMemberId()).isEqualTo(member.getId());
            assertThat(saved.getRestaurantId()).isEqualTo(restaurantId);
        }
    }

    @Nested
    class GetByMemberIdAndRestaurantId {

        @Test
        void 즐겨찾기_레스토랑을_조회한다() {
            Member member = createAndSaveMember("member");
            String restaurantId = "restaurant-id";
            favoriteRestaurantRepository.save(new FavoriteRestaurant(member.getId(), restaurantId));

            FavoriteRestaurant result = favoriteRestaurantDomainService.getByMemberIdAndRestaurantId(
                    member.getId(), restaurantId
            );

            assertThat(result.getMemberId()).isEqualTo(member.getId());
            assertThat(result.getRestaurantId()).isEqualTo(restaurantId);
        }

        @Test
        void 존재하지_않으면_예외가_발생한다() {
            Member member = createAndSaveMember("member");
            String restaurantId = "restaurant-id";

            assertThatThrownBy(() ->
                    favoriteRestaurantDomainService.getByMemberIdAndRestaurantId(
                            member.getId(), restaurantId
                    )
            ).isInstanceOf(MemberException.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void 즐겨찾기_레스토랑을_삭제한다() {
            Member member = createAndSaveMember("member");
            String restaurantId = "restaurant-id";
            FavoriteRestaurant favoriteRestaurant = favoriteRestaurantRepository.save(
                    new FavoriteRestaurant(member.getId(), restaurantId)
            );

            favoriteRestaurantDomainService.delete(favoriteRestaurant);

            boolean exists = favoriteRestaurantRepository.existsByMemberIdAndRestaurantId(
                    member.getId(), restaurantId
            );
            assertThat(exists).isFalse();
        }
    }

    private Member createAndSaveMember(String name) {
        Member member = new Member(name, "nickname", "email@example.com", "010-1234-5678");
        return memberRepository.save(member);
    }
}
