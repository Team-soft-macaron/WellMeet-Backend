package com.wellmeet.restaurant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.restaurant.RestaurantDomainService;
import com.wellmeet.domain.restaurant.businesshour.entity.BusinessHour;
import com.wellmeet.domain.restaurant.businesshour.entity.BusinessHours;
import com.wellmeet.domain.restaurant.businesshour.entity.DayOfWeek;
import com.wellmeet.domain.restaurant.entity.Restaurant;
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
    private RestaurantDomainService restaurantDomainService;

    @Mock
    private EventPublishService eventPublishService;

    @InjectMocks
    private RestaurantService restaurantService;

    @Nested
    class GetOperatingHours {

        @Test
        void 영업시간을_조회한다() {
            String restaurantId = "restaurant-1";
            Restaurant restaurant = createRestaurant(restaurantId);
            List<BusinessHour> businessHourList = createBusinessHourList(restaurant);
            BusinessHours businessHours = new BusinessHours(businessHourList);

            when(restaurantDomainService.getOperatingHours(restaurantId))
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
            Restaurant restaurant = createRestaurant(restaurantId);
            List<BusinessHour> businessHourList = createBusinessHourList(restaurant);
            BusinessHours businessHours = new BusinessHours(businessHourList);

            List<DayHours> dayHoursList = List.of(
                    new DayHours(DayOfWeek.MONDAY, true, LocalTime.of(9, 0), LocalTime.of(22, 0),
                            LocalTime.of(15, 0), LocalTime.of(17, 0)),
                    new DayHours(DayOfWeek.TUESDAY, true, LocalTime.of(9, 0), LocalTime.of(22, 0),
                            LocalTime.of(15, 0), LocalTime.of(17, 0)),
                    new DayHours(DayOfWeek.WEDNESDAY, true, LocalTime.of(9, 0), LocalTime.of(22, 0),
                            LocalTime.of(15, 0), LocalTime.of(17, 0)),
                    new DayHours(DayOfWeek.THURSDAY, true, LocalTime.of(9, 0), LocalTime.of(22, 0),
                            LocalTime.of(15, 0), LocalTime.of(17, 0)),
                    new DayHours(DayOfWeek.FRIDAY, true, LocalTime.of(9, 0), LocalTime.of(22, 0),
                            LocalTime.of(15, 0), LocalTime.of(17, 0)),
                    new DayHours(DayOfWeek.SATURDAY, false, null, null, null, null),
                    new DayHours(DayOfWeek.SUNDAY, false, null, null, null, null)
            );
            UpdateOperatingHoursRequest request = new UpdateOperatingHoursRequest(dayHoursList);

            when(restaurantDomainService.getOperatingHours(restaurantId))
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
            Restaurant restaurant = createRestaurant(restaurantId);

            UpdateRestaurantRequest request = new UpdateRestaurantRequest(
                    "수정된 식당",
                    "서울시 강남구",
                    37.5,
                    127.0,
                    "new-thumbnail.jpg"
            );

            when(restaurantDomainService.getById(restaurantId))
                    .thenReturn(restaurant);

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
            Restaurant restaurant = createRestaurant(restaurantId);

            UpdateRestaurantRequest request = new UpdateRestaurantRequest(
                    "수정된 식당",
                    "서울시 강남구",
                    37.5,
                    127.0,
                    "new-thumbnail.jpg"
            );

            when(restaurantDomainService.getById(restaurantId))
                    .thenReturn(restaurant);

            restaurantService.updateRestaurant(restaurantId, request);

            verify(eventPublishService).publishRestaurantUpdatedEvent(any(RestaurantUpdatedEvent.class));
        }
    }

    private Restaurant createRestaurant(String restaurantId) {
        Owner owner = new Owner("owner-name", "owner@email.com");
        return new Restaurant(
                restaurantId,
                "Test Restaurant",
                "서울시",
                37.5,
                127.0,
                "thumbnail.jpg",
                owner.getId()
        );
    }

    private List<BusinessHour> createBusinessHourList(Restaurant restaurant) {
        return List.of(
                new BusinessHour(DayOfWeek.MONDAY, true, LocalTime.of(9, 0), LocalTime.of(22, 0),
                        LocalTime.of(15, 0), LocalTime.of(17, 0), restaurant),
                new BusinessHour(DayOfWeek.TUESDAY, true, LocalTime.of(9, 0), LocalTime.of(22, 0),
                        LocalTime.of(15, 0), LocalTime.of(17, 0), restaurant),
                new BusinessHour(DayOfWeek.WEDNESDAY, true, LocalTime.of(9, 0), LocalTime.of(22, 0),
                        LocalTime.of(15, 0), LocalTime.of(17, 0), restaurant),
                new BusinessHour(DayOfWeek.THURSDAY, true, LocalTime.of(9, 0), LocalTime.of(22, 0),
                        LocalTime.of(15, 0), LocalTime.of(17, 0), restaurant),
                new BusinessHour(DayOfWeek.FRIDAY, true, LocalTime.of(9, 0), LocalTime.of(22, 0),
                        LocalTime.of(15, 0), LocalTime.of(17, 0), restaurant),
                new BusinessHour(DayOfWeek.SATURDAY, false, null, null, null, null, restaurant),
                new BusinessHour(DayOfWeek.SUNDAY, false, null, null, null, null, restaurant)
        );
    }
}
