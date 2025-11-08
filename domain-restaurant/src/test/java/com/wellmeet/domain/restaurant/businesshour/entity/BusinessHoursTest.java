package com.wellmeet.domain.restaurant.businesshour.entity;

import static org.assertj.core.api.Assertions.*;

import com.wellmeet.domain.businesshour.entity.BusinessHour;
import com.wellmeet.domain.businesshour.entity.BusinessHours;
import com.wellmeet.domain.businesshour.entity.DayOfWeek;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BusinessHoursTest {

    @Nested
    class Constructor {

        @Test
        void 영업시간_목록을_요일_순서대로_정렬한다() {
            Restaurant restaurant = createRestaurant();
            BusinessHour sunday = createBusinessHour(DayOfWeek.SUNDAY, restaurant);
            BusinessHour monday = createBusinessHour(DayOfWeek.MONDAY, restaurant);
            BusinessHour friday = createBusinessHour(DayOfWeek.FRIDAY, restaurant);
            BusinessHour wednesday = createBusinessHour(DayOfWeek.WEDNESDAY, restaurant);

            BusinessHours businessHours = new BusinessHours(List.of(friday, monday, sunday, wednesday));

            assertThat(businessHours.getValue())
                    .hasSize(4)
                    .extracting(BusinessHour::getDayOfWeek)
                    .containsExactly(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY, DayOfWeek.SUNDAY);
        }

        @Test
        void 빈_영업시간_목록으로_생성할_수_있다() {
            BusinessHours businessHours = new BusinessHours(List.of());

            assertThat(businessHours.getValue()).isEmpty();
        }
    }

    private Restaurant createRestaurant() {
        String ownerId = "test-owner-id";
        return new Restaurant("restaurant", "description", "address", 37.5, 127.0, "thumbnail", ownerId);
    }

    private BusinessHour createBusinessHour(DayOfWeek dayOfWeek, Restaurant restaurant) {
        return new BusinessHour(dayOfWeek, true, LocalTime.of(9, 0), LocalTime.of(22, 0), LocalTime.of(14, 0),
                LocalTime.of(15, 0), restaurant);
    }
}
