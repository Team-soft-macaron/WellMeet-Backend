package com.wellmeet.favorite;

import com.wellmeet.client.MemberFavoriteRestaurantFeignClient;
import com.wellmeet.client.RestaurantFeignClient;
import com.wellmeet.client.dto.request.RestaurantIdsRequest;
import com.wellmeet.common.dto.FavoriteRestaurantDTO;
import com.wellmeet.common.dto.RestaurantDTO;
import com.wellmeet.favorite.dto.FavoriteRestaurantResponse;
import com.wellmeet.favorite.saga.FavoriteAddContext;
import com.wellmeet.favorite.saga.FavoriteAddSagaFactory;
import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.core.SagaDefinition;
import com.wellmeet.saga.orchestrator.SagaExecutionException;
import com.wellmeet.saga.orchestrator.SagaOrchestrator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserFavoriteRestaurantBffService {

    private final MemberFavoriteRestaurantFeignClient favoriteRestaurantClient;
    private final RestaurantFeignClient restaurantClient;
    private final SagaOrchestrator sagaOrchestrator;
    private final FavoriteAddSagaFactory favoriteAddSagaFactory;

    public List<FavoriteRestaurantResponse> getFavoriteRestaurants(String memberId) {
        List<FavoriteRestaurantDTO> favoriteRestaurants = favoriteRestaurantClient.getFavoritesByMemberId(memberId);
        if (favoriteRestaurants.isEmpty()) {
            return List.of();
        }

        List<String> restaurantIds = favoriteRestaurants.stream()
                .map(FavoriteRestaurantDTO::restaurantId)
                .toList();

        Map<String, RestaurantDTO> restaurantsById = restaurantClient
                .getRestaurantsByIds(new RestaurantIdsRequest(restaurantIds))
                .stream()
                .collect(Collectors.toMap(RestaurantDTO::id, Function.identity()));

        return favoriteRestaurants.stream()
                .map(favoriteRestaurant -> {
                    RestaurantDTO restaurant = restaurantsById.get(favoriteRestaurant.restaurantId());
                    return getFavoriteRestaurantResponse(restaurant);
                })
                .toList();
    }

    private FavoriteRestaurantResponse getFavoriteRestaurantResponse(RestaurantDTO restaurant) {
        Double rating = restaurantClient.getAverageRating(restaurant.id());
        double ratingValue = (rating != null) ? rating : 0.0;
        return new FavoriteRestaurantResponse(restaurant, ratingValue);
    }

    public FavoriteRestaurantResponse addFavoriteRestaurant(String memberId, String restaurantId) {
        // 1. Restaurant 조회
        RestaurantDTO restaurant = restaurantClient.getRestaurant(restaurantId);

        // 2. Saga 실행 (Favorite 추가)
        String sagaId = UUID.randomUUID().toString();
        String idempotencyKey = String.format("favorite:add:%s:%s", memberId, restaurantId);

        FavoriteAddContext favoriteAddContext = new FavoriteAddContext(memberId, restaurantId);

        SagaContext context = SagaContext.builder()
                .sagaId(sagaId)
                .idempotencyKey(idempotencyKey)
                .put("favoriteAddContext", favoriteAddContext)
                .build();

        SagaDefinition<String> saga = favoriteAddSagaFactory.createSaga();

        try {
            sagaOrchestrator.execute(saga, context);
            return getFavoriteRestaurantResponse(restaurant);
        } catch (SagaExecutionException e) {
            log.error("Saga execution failed: sagaId={}, error={}", sagaId, e.getMessage());
            throw new RuntimeException("즐겨찾기 추가 중 오류가 발생했습니다.", e);
        }
    }

    public void removeFavoriteRestaurant(String memberId, String restaurantId) {
        favoriteRestaurantClient.removeFavorite(memberId, restaurantId);
    }
}
