package com.wellmeet.restaurant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wellmeet.client.RestaurantClient;
import com.wellmeet.client.dto.BusinessHourDTO;
import com.wellmeet.client.dto.RestaurantDTO;
import com.wellmeet.client.dto.request.UpdateOperatingHoursDTO;
import com.wellmeet.client.dto.request.UpdateRestaurantDTO;
import com.wellmeet.common.DayOfWeek;
import com.wellmeet.global.event.EventPublishService;
import com.wellmeet.global.event.event.RestaurantUpdatedEvent;
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
class RestaurantServiceTest {

    @Mock
    private RestaurantClient restaurantClient;

    @Mock
    private EventPublishService eventPublishService;

    @InjectMocks
    private RestaurantService restaurantService;

    @Nested
    class GetOperatingHours {

        @Test
        void 영업시간을_조회한다() {
            String restaurantId = "restaurant-1";
            List<BusinessHourDTO> businessHours = createBusinessHourDTOList(restaurantId);

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
            List<BusinessHourDTO> businessHours = createBusinessHourDTOList(restaurantId);

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

            RestaurantDTO restaurantDTO = RestaurantDTO.builder()
                    .id(restaurantId)
                    .name("수정된 식당")
                    .address("서울시 강남구")
                    .latitude(37.5)
                    .longitude(127.0)
                    .thumbnail("new-thumbnail.jpg")
                    .ownerId("owner-1")
                    .build();

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

            RestaurantDTO restaurantDTO = RestaurantDTO.builder()
                    .id(restaurantId)
                    .name("수정된 식당")
                    .address("서울시 강남구")
                    .latitude(37.5)
                    .longitude(127.0)
                    .thumbnail("new-thumbnail.jpg")
                    .ownerId("owner-1")
                    .build();

            when(restaurantClient.updateRestaurant(eq(restaurantId), any(UpdateRestaurantDTO.class)))
                    .thenReturn(restaurantDTO);

            restaurantService.updateRestaurant(restaurantId, request);

            verify(eventPublishService).publishRestaurantUpdatedEvent(any(RestaurantUpdatedEvent.class));
        }
    }

    private List<BusinessHourDTO> createBusinessHourDTOList(String restaurantId) {
        return List.of(
                BusinessHourDTO.builder()
                        .id(1L)
                        .dayOfWeek("MONDAY")
                        .isOperating(true)
                        .open(LocalTime.of(9, 0))
                        .close(LocalTime.of(22, 0))
                        .breakStart(LocalTime.of(15, 0))
                        .breakEnd(LocalTime.of(17, 0))
                        .build(),
                BusinessHourDTO.builder()
                        .id(2L)
                        .dayOfWeek("TUESDAY")
                        .isOperating(true)
                        .open(LocalTime.of(9, 0))
                        .close(LocalTime.of(22, 0))
                        .breakStart(LocalTime.of(15, 0))
                        .breakEnd(LocalTime.of(17, 0))
                        .build(),
                BusinessHourDTO.builder()
                        .id(3L)
                        .dayOfWeek("WEDNESDAY")
                        .isOperating(true)
                        .open(LocalTime.of(9, 0))
                        .close(LocalTime.of(22, 0))
                        .breakStart(LocalTime.of(15, 0))
                        .breakEnd(LocalTime.of(17, 0))
                        .build(),
                BusinessHourDTO.builder()
                        .id(4L)
                        .dayOfWeek("THURSDAY")
                        .isOperating(true)
                        .open(LocalTime.of(9, 0))
                        .close(LocalTime.of(22, 0))
                        .breakStart(LocalTime.of(15, 0))
                        .breakEnd(LocalTime.of(17, 0))
                        .build(),
                BusinessHourDTO.builder()
                        .id(5L)
                        .dayOfWeek("FRIDAY")
                        .isOperating(true)
                        .open(LocalTime.of(9, 0))
                        .close(LocalTime.of(22, 0))
                        .breakStart(LocalTime.of(15, 0))
                        .breakEnd(LocalTime.of(17, 0))
                        .build(),
                BusinessHourDTO.builder()
                        .id(6L)
                        .dayOfWeek("SATURDAY")
                        .isOperating(false)
                        .open(null)
                        .close(null)
                        .breakStart(null)
                        .breakEnd(null)
                        .build(),
                BusinessHourDTO.builder()
                        .id(7L)
                        .dayOfWeek("SUNDAY")
                        .isOperating(false)
                        .open(null)
                        .close(null)
                        .breakStart(null)
                        .breakEnd(null)
                        .build()
        );
    }
}
