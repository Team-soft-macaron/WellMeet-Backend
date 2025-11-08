package com.wellmeet.domain.restaurant.businesshour;

import static org.assertj.core.api.Assertions.*;

import com.wellmeet.BaseRepositoryTest;

import com.wellmeet.domain.businesshour.domainservice.BusinessHourDomainService;
import com.wellmeet.domain.businesshour.entity.BusinessHour;
import com.wellmeet.domain.businesshour.entity.BusinessHours;
import com.wellmeet.domain.businesshour.entity.DayOfWeek;
import com.wellmeet.domain.businesshour.repository.BusinessHourRepository;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.repository.RestaurantRepository;
import java.time.LocalTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import(BusinessHourDomainService.class)
class BusinessHourDomainServiceTest extends BaseRepositoryTest {

    @Autowired
    private BusinessHourDomainService businessHourDomainService;

    @Autowired
    private BusinessHourRepository businessHourRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Nested
    class GetOperatingHours {

        @Test
        void 식당의_영업시간을_조회한다() {
            Restaurant restaurant = createAndSaveRestaurant("restaurant");
            createAndSaveBusinessHour(DayOfWeek.MONDAY, restaurant);
            createAndSaveBusinessHour(DayOfWeek.TUESDAY, restaurant);
            createAndSaveBusinessHour(DayOfWeek.WEDNESDAY, restaurant);

            BusinessHours result = businessHourDomainService.getOperatingHours(restaurant.getId());

            assertThat(result.getValue()).hasSize(3);
        }

        @Test
        void 영업시간이_없으면_빈_리스트를_반환한다() {
            Restaurant restaurant = createAndSaveRestaurant("restaurant");

            BusinessHours result = businessHourDomainService.getOperatingHours(restaurant.getId());

            assertThat(result.getValue()).isEmpty();
        }

        @Test
        void 영업시간을_요일_순서대로_정렬하여_반환한다() {
            Restaurant restaurant = createAndSaveRestaurant("restaurant");
            createAndSaveBusinessHour(DayOfWeek.FRIDAY, restaurant);
            createAndSaveBusinessHour(DayOfWeek.MONDAY, restaurant);
            createAndSaveBusinessHour(DayOfWeek.WEDNESDAY, restaurant);

            BusinessHours result = businessHourDomainService.getOperatingHours(restaurant.getId());

            assertThat(result.getValue())
                    .extracting(BusinessHour::getDayOfWeek)
                    .containsExactly(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY);
        }
    }

    private Restaurant createAndSaveRestaurant(String name) {
        String ownerId = "test-owner-id";
        Restaurant restaurant = new Restaurant(name, "description", "address", 37.5, 127.0, "thumbnail", ownerId);
        return restaurantRepository.save(restaurant);
    }

    private BusinessHour createAndSaveBusinessHour(DayOfWeek dayOfWeek, Restaurant restaurant) {
        BusinessHour businessHour = new BusinessHour(dayOfWeek, true, LocalTime.of(9, 0), LocalTime.of(22, 0),
                LocalTime.of(14, 0), LocalTime.of(15, 0), restaurant);
        return businessHourRepository.save(businessHour);
    }
}
