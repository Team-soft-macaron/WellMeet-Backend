package com.wellmeet.domain.restaurant.availabledate.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wellmeet.domain.restaurant.exception.RestaurantErrorCode;
import com.wellmeet.domain.restaurant.exception.RestaurantException;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AvailableDateTest {

    @Nested
    class ReserveParty {

        @Test
        void 예약_인원만큼_가용_인원이_줄어든다() {
            int maxCapacity = 10;
            AvailableDate availableDate = new AvailableDate(
                    LocalDate.now().plusDays(1),
                    LocalTime.now(),
                    maxCapacity,
                    null
            );
            int partySize = 5;
            availableDate.reduceCapacity(partySize);

            assertAll(
                    () -> assertThat(availableDate.getMaxCapacity()).isEqualTo(maxCapacity - partySize),
                    () -> assertThat(availableDate.isAvailable()).isTrue()
            );
        }

        @Test
        void 가용_인원이_0이면_예약_불가능하다() {
            int maxCapacity = 10;
            AvailableDate availableDate = new AvailableDate(
                    LocalDate.now().plusDays(1),
                    LocalTime.now(),
                    maxCapacity,
                    null
            );
            int partySize = 10;
            availableDate.reduceCapacity(partySize);

            assertAll(
                    () -> assertThat(availableDate.getMaxCapacity()).isEqualTo(maxCapacity - partySize),
                    () -> assertThat(availableDate.isAvailable()).isFalse()
            );
        }

        @Test
        void 가용_인원_수보다_많은_인원이_예약할_수_없다() {
            int maxCapacity = 10;
            AvailableDate availableDate = new AvailableDate(
                    LocalDate.now().plusDays(1),
                    LocalTime.now(),
                    maxCapacity,
                    null
            );

            assertThatThrownBy(() -> availableDate.reduceCapacity(maxCapacity + 1))
                    .isInstanceOf(RestaurantException.class)
                    .hasMessage(RestaurantErrorCode.NOT_ENOUGH_CAPACITY.getMessage());
        }
    }
}
