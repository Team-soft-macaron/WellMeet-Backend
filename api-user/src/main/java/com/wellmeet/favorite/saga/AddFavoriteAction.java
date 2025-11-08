package com.wellmeet.favorite.saga;

import com.wellmeet.client.MemberFavoriteRestaurantFeignClient;
import com.wellmeet.saga.core.SagaAction;
import com.wellmeet.saga.core.SagaContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddFavoriteAction implements SagaAction<String> {

    private final MemberFavoriteRestaurantFeignClient favoriteRestaurantClient;

    @Override
    public String execute(SagaContext context) {
        FavoriteAddContext ctx = (FavoriteAddContext) context.getData().get("favoriteAddContext");
        favoriteRestaurantClient.addFavorite(ctx.memberId(), ctx.restaurantId());
        return "favorite_added";
    }
}
