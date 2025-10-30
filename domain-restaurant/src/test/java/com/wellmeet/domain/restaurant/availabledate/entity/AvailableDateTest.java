package com.wellmeet.domain.restaurant.availabledate.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AvailableDateTest {

    @Nested
    class CanNotReserve {

        @Test
        void 예약_인원수가_가용인원보다_크면_예약이_불가능하다() {
            int maxCapacity = 10;
            AvailableDate availableDate = new AvailableDate(
                    LocalDate.now(),
                    LocalTime.now(),
                    maxCapacity,
                    null
            );
            int partySize = maxCapacity + 1;
            boolean canNotReserve = availableDate.canNotReserve(partySize);

            assertThat(canNotReserve).isTrue();
        }
    }
}
