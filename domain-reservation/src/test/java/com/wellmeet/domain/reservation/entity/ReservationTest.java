package com.wellmeet.domain.reservation.entity;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wellmeet.domain.reservation.exception.ReservationErrorCode;
import com.wellmeet.domain.reservation.exception.ReservationException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ReservationTest {

    @Nested
    class ValidatePartySize {

        @Test
        void 예약_인원은_0이하일_수_없다() {
            assertThatThrownBy(() -> new Reservation(null, null, null, 0, "request"))
                    .isInstanceOf(ReservationException.class)
                    .hasMessage(ReservationErrorCode.PARTY_SIZE_INVALID.getMessage());
        }
    }

    @Nested
    class ValidateRequest {

        @Test
        void 예약_요청_사항은_일정_길이_이내여아한다() {
            String request = "r".repeat(Reservation.MAX_REQUEST_LENGTH + 1);

            assertThatThrownBy(() -> new Reservation(null, null, null, 1, request))
                    .isInstanceOf(ReservationException.class)
                    .hasMessage(ReservationErrorCode.REQUEST_INVALID.getMessage());
        }
    }
}
