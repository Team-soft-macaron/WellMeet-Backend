package com.wellmeet.favorite.saga;

import com.wellmeet.client.MemberFavoriteRestaurantFeignClient;
import com.wellmeet.saga.core.SagaAction;
import com.wellmeet.saga.core.SagaContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoveFavoriteCompensation implements SagaAction<Void> {

    private final MemberFavoriteRestaurantFeignClient favoriteRestaurantClient;

    @Override
    public Void execute(SagaContext context) {
        FavoriteAddContext ctx = (FavoriteAddContext) context.getData().get("favoriteAddContext");
        favoriteRestaurantClient.removeFavorite(ctx.memberId(), ctx.restaurantId());
        return null;
    }
}
