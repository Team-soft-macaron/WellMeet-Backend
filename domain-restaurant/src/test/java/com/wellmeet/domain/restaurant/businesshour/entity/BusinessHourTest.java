package com.wellmeet.domain.restaurant.businesshour.entity;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wellmeet.domain.businesshour.entity.BusinessHour;
import com.wellmeet.domain.businesshour.entity.DayOfWeek;
import com.wellmeet.domain.exception.RestaurantErrorCode;
import com.wellmeet.domain.exception.RestaurantException;
import java.time.LocalTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BusinessHourTest {

    @Nested
    class ValidateTime {

        @Test
        void 오픈_시간은_마감_시간보다_늦을_수_없다() {
            LocalTime openingTime = LocalTime.of(10, 0);
            LocalTime closingTime = LocalTime.of(9, 0);

            assertThatThrownBy(() -> new BusinessHour(DayOfWeek.MONDAY, true, openingTime, closingTime, LocalTime.now(),
                    LocalTime.now().plusHours(1), null))
                    .isInstanceOf(RestaurantException.class)
                    .hasMessage(RestaurantErrorCode.TIME_SEQUENCE_INVALID.getMessage());
        }

        @Test
        void 휴식_시작_시간은_휴식_종료_시간보다_늦을_수_없다() {
            LocalTime breakStartTime = LocalTime.of(14, 0);
            LocalTime breakEndTime = LocalTime.of(13, 0);

            assertThatThrownBy(
                    () -> new BusinessHour(DayOfWeek.MONDAY, true, LocalTime.of(10, 0), LocalTime.of(20, 0),
                            breakStartTime,
                            breakEndTime, null))
                    .isInstanceOf(RestaurantException.class)
                    .hasMessage(RestaurantErrorCode.TIME_SEQUENCE_INVALID.getMessage());
        }
    }

    @Nested
    class ValidateBreakTime {

        @Test
        void 휴식_시간은_운영_시간_이내여야_한다() {
            LocalTime openingTime = LocalTime.of(10, 0);
            LocalTime closingTime = LocalTime.of(20, 0);

            assertAll(
                    () -> assertThatThrownBy(() -> new BusinessHour(
                            DayOfWeek.MONDAY, true, openingTime, closingTime, openingTime.minusHours(1),
                            closingTime.minusHours(1), null
                    )).isInstanceOf(RestaurantException.class)
                            .hasMessage(RestaurantErrorCode.TIME_SEQUENCE_INVALID.getMessage()),

                    () -> assertThatThrownBy(() -> new BusinessHour(
                            DayOfWeek.MONDAY, true, openingTime, closingTime, openingTime.plusHours(1),
                            closingTime.plusHours(1), null
                    )).isInstanceOf(RestaurantException.class)
                            .hasMessage(RestaurantErrorCode.TIME_SEQUENCE_INVALID.getMessage())
            );
        }
    }
}
