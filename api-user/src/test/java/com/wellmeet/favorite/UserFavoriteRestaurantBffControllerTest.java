package com.wellmeet.favorite;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wellmeet.favorite.dto.FavoriteRestaurantResponse;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserFavoriteRestaurantBffController.class)
class UserFavoriteRestaurantBffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserFavoriteRestaurantBffService favoriteService;

    @Nested
    class GetFavoriteRestaurants {

        @Test
        void 즐겨찾기_레스토랑_조회() throws Exception {
            String memberId = "member-1";
            FavoriteRestaurantResponse response1 = createFavoriteRestaurantResponse("restaurant-1", "식당1", 4.5);
            FavoriteRestaurantResponse response2 = createFavoriteRestaurantResponse("restaurant-2", "식당2", 3.8);

            when(favoriteService.getFavoriteRestaurants(memberId))
                    .thenReturn(List.of(response1, response2));

            mockMvc.perform(get("/user/favorite/restaurant/list")
                            .queryParam("memberId", memberId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value("restaurant-1"))
                    .andExpect(jsonPath("$[0].name").value("식당1"))
                    .andExpect(jsonPath("$[1].id").value("restaurant-2"))
                    .andExpect(jsonPath("$[1].name").value("식당2"));

            verify(favoriteService).getFavoriteRestaurants(memberId);
        }
    }

    @Nested
    class AddFavoriteRestaurant {

        @Test
        void 즐겨찾기_레스토랑_추가() throws Exception {
            String memberId = "member-1";
            String restaurantId = "restaurant-1";
            FavoriteRestaurantResponse response = createFavoriteRestaurantResponse(restaurantId, "맛집", 4.2);

            when(favoriteService.addFavoriteRestaurant(memberId, restaurantId))
                    .thenReturn(response);

            mockMvc.perform(post("/user/favorite/restaurant/{restaurantId}", restaurantId)
                            .queryParam("memberId", memberId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(restaurantId))
                    .andExpect(jsonPath("$.name").value("맛집"))
                    .andExpect(jsonPath("$.rating").value(4.2));

            verify(favoriteService).addFavoriteRestaurant(memberId, restaurantId);
        }
    }

    @Nested
    class RemoveFavoriteRestaurant {

        @Test
        void 즐겨찾기_레스토랑_삭제() throws Exception {
            String memberId = "member-1";
            String restaurantId = "restaurant-1";

            mockMvc.perform(delete("/user/favorite/restaurant/{restaurantId}", restaurantId)
                            .queryParam("memberId", memberId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(favoriteService).removeFavoriteRestaurant(memberId, restaurantId);
        }
    }

    private FavoriteRestaurantResponse createFavoriteRestaurantResponse(String id, String name, double rating) {
        return FavoriteRestaurantResponse.builder()
                .id(id)
                .name(name)
                .address("서울시 강남구")
                .thumbnail("thumbnail.jpg")
                .rating(rating)
                .build();
    }
}
