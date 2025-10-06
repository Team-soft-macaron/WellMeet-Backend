package com.wellmeet.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DistanceCalculatorTest {

    @Nested
    class CalculateDistance {

        @Test
        void 같은_지점의_거리는_0이다() {
            double lat = 37.5665;
            double lon = 126.9780;

            double distance = DistanceCalculator.calculateDistance(lat, lon, lat, lon);

            assertThat(distance).isEqualTo(0.0);
        }

        @Test
        void 서울_시청과_강남역_사이의_거리를_계산한다() {
            double cityHallLat = 37.5665;
            double cityHallLon = 126.9780;
            double gangnamLat = 37.4979;
            double gangnamLon = 127.0276;

            double distance = DistanceCalculator.calculateDistance(
                    cityHallLat, cityHallLon,
                    gangnamLat, gangnamLon
            );

            assertThat(distance).isBetween(8000.0, 12000.0);
        }

        @Test
        void 짧은_거리를_정확하게_계산한다() {
            double lat1 = 37.5000;
            double lon1 = 127.0000;
            double lat2 = 37.5100;
            double lon2 = 127.0100;

            double distance = DistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2);

            assertThat(distance).isBetween(1000.0, 2000.0);
        }

        @Test
        void 위도만_다른_경우_거리를_계산한다() {
            double lat1 = 37.5000;
            double lon = 127.0000;
            double lat2 = 37.6000;

            double distance = DistanceCalculator.calculateDistance(lat1, lon, lat2, lon);

            assertThat(distance).isGreaterThan(0);
        }

        @Test
        void 경도만_다른_경우_거리를_계산한다() {
            double lat = 37.5000;
            double lon1 = 127.0000;
            double lon2 = 127.1000;

            double distance = DistanceCalculator.calculateDistance(lat, lon1, lat, lon2);

            assertThat(distance).isGreaterThan(0);
        }
    }
}
