package com.wellmeet.favorite.saga;

import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.core.SagaDefinition;
import com.wellmeet.saga.core.SagaStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FavoriteAddSagaFactory {

    private final AddFavoriteAction addFavoriteAction;
    private final RemoveFavoriteCompensation removeFavoriteCompensation;

    public SagaDefinition<String> createSaga() {
        return new SagaDefinition<String>("FAVORITE_ADD") {
            {
                addStep(SagaStep.builder()
                        .name("addFavorite")
                        .forwardAction(addFavoriteAction)
                        .compensationAction(removeFavoriteCompensation)
                        .maxRetries(3)
                        .retryDelayMs(1000L)
                        .build());
            }

            @Override
            public String buildResult(SagaContext context) {
                return "favorite_added";
            }
        };
    }
}
