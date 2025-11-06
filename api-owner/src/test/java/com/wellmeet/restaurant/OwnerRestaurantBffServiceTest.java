package com.wellmeet.restaurant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wellmeet.client.RestaurantFeignClient;
import com.wellmeet.common.dto.BusinessHourDTO;
import com.wellmeet.common.dto.RestaurantDTO;
import com.wellmeet.common.dto.request.UpdateOperatingHoursDTO;
import com.wellmeet.common.dto.request.UpdateRestaurantDTO;
import com.wellmeet.global.event.OwnerEventPublishBffService;
import com.wellmeet.global.event.event.RestaurantUpdatedEvent;
import com.wellmeet.reservation.dto.DayOfWeek;
import com.wellmeet.restaurant.dto.OperatingHoursResponse;
import com.wellmeet.restaurant.dto.UpdateOperatingHoursRequest;
import com.wellmeet.restaurant.dto.UpdateOperatingHoursRequest.DayHours;
import com.wellmeet.restaurant.dto.UpdateRestaurantRequest;
import com.wellmeet.restaurant.dto.UpdateRestaurantResponse;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OwnerRestaurantBffServiceTest {

    @Mock
    private RestaurantFeignClient restaurantClient;

    @Mock
    private OwnerEventPublishBffService eventPublishService;

    @InjectMocks
    private OwnerRestaurantBffService restaurantService;

    @Nested
    class GetOperatingHours {

        @Test
        void 영업시간을_조회한다() {
            String restaurantId = "restaurant-1";
            List<BusinessHourDTO> businessHours = createBusinessHourDTOList();

            when(restaurantClient.getOperatingHours(restaurantId))
                    .thenReturn(businessHours);

            OperatingHoursResponse response = restaurantService.getOperatingHours(restaurantId);

            assertThat(response).isNotNull();
            assertThat(response.getOperatingHours()).hasSize(7);
        }
    }

    @Nested
    class UpdateOperatingHours {

        @Test
        void 영업시간을_수정한다() {
            String restaurantId = "restaurant-1";
            List<BusinessHourDTO> businessHours = createBusinessHourDTOList();

            List<DayHours> dayHoursList = List.of(
                    new DayHours(DayOfWeek.MONDAY, true,
                            LocalTime.of(9, 0), LocalTime.of(22, 0),
                            LocalTime.of(15, 0), LocalTime.of(17, 0)),
                    new DayHours(DayOfWeek.TUESDAY, true,
                            LocalTime.of(9, 0), LocalTime.of(22, 0),
                            LocalTime.of(15, 0), LocalTime.of(17, 0)),
                    new DayHours(DayOfWeek.WEDNESDAY, true,
                            LocalTime.of(9, 0), LocalTime.of(22, 0),
                            LocalTime.of(15, 0), LocalTime.of(17, 0)),
                    new DayHours(DayOfWeek.THURSDAY, true,
                            LocalTime.of(9, 0), LocalTime.of(22, 0),
                            LocalTime.of(15, 0), LocalTime.of(17, 0)),
                    new DayHours(DayOfWeek.FRIDAY, true,
                            LocalTime.of(9, 0), LocalTime.of(22, 0),
                            LocalTime.of(15, 0), LocalTime.of(17, 0)),
                    new DayHours(DayOfWeek.SATURDAY, false, null,
                            null, null, null),
                    new DayHours(DayOfWeek.SUNDAY, false, null,
                            null, null, null)
            );
            UpdateOperatingHoursRequest request = new UpdateOperatingHoursRequest(dayHoursList);

            when(restaurantClient.updateOperatingHours(eq(restaurantId), any(UpdateOperatingHoursDTO.class)))
                    .thenReturn(businessHours);

            OperatingHoursResponse response = restaurantService.updateOperatingHours(restaurantId, request);

            assertThat(response).isNotNull();
            assertThat(response.getOperatingHours()).hasSize(7);
        }
    }

    @Nested
    class UpdateRestaurant {

        @Test
        void 식당_메타데이터를_수정한다() {
            String restaurantId = "restaurant-1";

            UpdateRestaurantRequest request = new UpdateRestaurantRequest(
                    "수정된 식당",
                    "서울시 강남구",
                    37.5,
                    127.0,
                    "new-thumbnail.jpg"
            );

            RestaurantDTO restaurantDTO = new RestaurantDTO(
                    restaurantId,
                    "수정된 식당",
                    "서울시 강남구",
                    37.5,
                    127.0,
                    "new-thumbnail.jpg",
                    "owner-1",
                    null,
                    null
            );

            when(restaurantClient.updateRestaurant(eq(restaurantId), any(UpdateRestaurantDTO.class)))
                    .thenReturn(restaurantDTO);

            UpdateRestaurantResponse response = restaurantService.updateRestaurant(restaurantId, request);

            assertThat(response.getName()).isEqualTo("수정된 식당");
            assertThat(response.getAddress()).isEqualTo("서울시 강남구");
            assertThat(response.getLatitude()).isEqualTo(37.5);
            assertThat(response.getLongitude()).isEqualTo(127.0);
            assertThat(response.getThumbnail()).isEqualTo("new-thumbnail.jpg");
        }

        @Test
        void 식당_수정_시_이벤트를_발행한다() {
            String restaurantId = "restaurant-1";

            UpdateRestaurantRequest request = new UpdateRestaurantRequest(
                    "수정된 식당",
                    "서울시 강남구",
                    37.5,
                    127.0,
                    "new-thumbnail.jpg"
            );

            RestaurantDTO restaurantDTO = new RestaurantDTO(
                    restaurantId,
                    "수정된 식당",
                    "서울시 강남구",
                    37.5,
                    127.0,
                    "new-thumbnail.jpg",
                    "owner-1",
                    null,
                    null
            );

            when(restaurantClient.updateRestaurant(eq(restaurantId), any(UpdateRestaurantDTO.class)))
                    .thenReturn(restaurantDTO);

            restaurantService.updateRestaurant(restaurantId, request);

            verify(eventPublishService).publishRestaurantUpdatedEvent(any(RestaurantUpdatedEvent.class));
        }
    }

    private List<BusinessHourDTO> createBusinessHourDTOList() {
        return List.of(
                new BusinessHourDTO(1L, java.time.DayOfWeek.MONDAY, true,
                        LocalTime.of(9, 0), LocalTime.of(22, 0),
                        LocalTime.of(15, 0), LocalTime.of(17, 0), null, null, null),
                new BusinessHourDTO(2L, java.time.DayOfWeek.TUESDAY, true,
                        LocalTime.of(9, 0), LocalTime.of(22, 0),
                        LocalTime.of(15, 0), LocalTime.of(17, 0), null, null, null),
                new BusinessHourDTO(3L, java.time.DayOfWeek.WEDNESDAY, true,
                        LocalTime.of(9, 0), LocalTime.of(22, 0),
                        LocalTime.of(15, 0), LocalTime.of(17, 0), null, null, null),
                new BusinessHourDTO(4L, java.time.DayOfWeek.THURSDAY, true,
                        LocalTime.of(9, 0), LocalTime.of(22, 0),
                        LocalTime.of(15, 0), LocalTime.of(17, 0), null, null, null),
                new BusinessHourDTO(5L, java.time.DayOfWeek.FRIDAY, true,
                        LocalTime.of(9, 0), LocalTime.of(22, 0),
                        LocalTime.of(15, 0), LocalTime.of(17, 0), null, null, null),
                new BusinessHourDTO(6L, java.time.DayOfWeek.SATURDAY, false,
                        null, null, null, null, null, null, null),
                new BusinessHourDTO(7L, java.time.DayOfWeek.SUNDAY, false,
                        null, null, null, null, null, null, null)
        );
    }
}
