package com.wellmeet.restaurant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.wellmeet.BaseControllerTest;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class UserRestaurantBffControllerTest extends BaseControllerTest {

    @MockitoBean
    private UserRestaurantBffService restaurantService;

    @Nested
    class GetOperatingHours {

        @Test
        void 식당의_운영시간을_불러온다() {
            String restaurantId = "restaurant-1";
            String ownerId = "owner-1";

            OperatingHoursResponse mockResponse = createOperatingHoursResponse();
            when(restaurantService.getOperatingHours(restaurantId))
                    .thenReturn(mockResponse);

            OperatingHoursResponse response = given()
                    .pathParam("restaurantId", restaurantId)
                    .queryParam("ownerId", ownerId)
                    .when().get("/owner/restaurant/{restaurantId}/operating-hours")
                    .then().statusCode(200)
                    .extract().as(OperatingHoursResponse.class);

            assertThat(response.getOperatingHours()).hasSize(3);
            assertThat(response.getOperatingHours().get(0).getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
            assertThat(response.getOperatingHours().get(1).getDayOfWeek()).isEqualTo(DayOfWeek.TUESDAY);
            assertThat(response.getOperatingHours().get(2).getDayOfWeek()).isEqualTo(DayOfWeek.WEDNESDAY);
        }
    }

    @Nested
    class UpdateOperatingHours {

        @Test
        void 식당의_운영시간을_업데이트한다() {
            String restaurantId = "restaurant-1";
            String ownerId = "owner-1";

            UpdateOperatingHoursRequest request = new UpdateOperatingHoursRequest(
                    List.of(
                            new DayHours(DayOfWeek.MONDAY, true, LocalTime.of(9, 0), LocalTime.of(21, 0),
                                    LocalTime.of(13, 0), LocalTime.of(14, 0)),
                            new DayHours(DayOfWeek.TUESDAY, false, null, null, null, null),
                            new DayHours(DayOfWeek.WEDNESDAY, true, LocalTime.of(10, 0), LocalTime.of(20, 0),
                                    LocalTime.of(13, 0), LocalTime.of(14, 0))
                    )
            );

            OperatingHoursResponse mockResponse = createUpdatedOperatingHoursResponse();
            when(restaurantService.updateOperatingHours(eq(restaurantId), any(UpdateOperatingHoursRequest.class)))
                    .thenReturn(mockResponse);

            OperatingHoursResponse response = given()
                    .contentType("application/json")
                    .pathParam("restaurantId", restaurantId)
                    .queryParam("ownerId", ownerId)
                    .body(request)
                    .when().put("/owner/restaurant/{restaurantId}/operating-hours")
                    .then().statusCode(200)
                    .extract().as(OperatingHoursResponse.class);

            assertThat(response.getOperatingHours()).hasSize(3);
            assertThat(response.getOperatingHours().get(0).getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
            assertThat(response.getOperatingHours().get(0).isOperating()).isTrue();
            assertThat(response.getOperatingHours().get(0).getOpen()).isEqualTo("09:00");
            assertThat(response.getOperatingHours().get(0).getClose()).isEqualTo("21:00");
        }
    }

    @Nested
    class UpdateRestaurant {

        @Test
        void 식당_정보를_갱신한다() {
            String restaurantId = "restaurant-1";
            String ownerId = "owner-1";
            String newRestaurantName = "new restaurant";

            UpdateRestaurantRequest request = new UpdateRestaurantRequest(
                    newRestaurantName,
                    "address",
                    36.5,
                    128.0,
                    "thumbnail"
            );

            UpdateRestaurantResponse mockResponse = new UpdateRestaurantResponse(
                    newRestaurantName,
                    "address",
                    36.5,
                    128.0,
                    "thumbnail"
            );

            when(restaurantService.updateRestaurant(eq(restaurantId), any(UpdateRestaurantRequest.class)))
                    .thenReturn(mockResponse);

            UpdateRestaurantResponse response = given()
                    .contentType("application/json")
                    .pathParam("restaurantId", restaurantId)
                    .queryParam("ownerId", ownerId)
                    .body(request)
                    .when().put("/owner/restaurant/{restaurantId}")
                    .then().statusCode(200)
                    .extract().as(UpdateRestaurantResponse.class);

            assertThat(response.getName()).isEqualTo(newRestaurantName);
        }
    }

    private OperatingHoursResponse createOperatingHoursResponse() {
        OperatingHoursResponse response = new OperatingHoursResponse();
        List<OperatingHoursResponse.DayHours> dayHoursList = List.of(
                createDayHours(DayOfWeek.MONDAY, true, "09:00", "22:00", "15:00", "17:00"),
                createDayHours(DayOfWeek.TUESDAY, true, "09:00", "22:00", "15:00", "17:00"),
                createDayHours(DayOfWeek.WEDNESDAY, true, "09:00", "22:00", "15:00", "17:00")
        );

        try {
            java.lang.reflect.Field field = OperatingHoursResponse.class.getDeclaredField("operatingHours");
            field.setAccessible(true);
            field.set(response, dayHoursList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    private OperatingHoursResponse createUpdatedOperatingHoursResponse() {
        OperatingHoursResponse response = new OperatingHoursResponse();
        List<OperatingHoursResponse.DayHours> dayHoursList = List.of(
                createDayHours(DayOfWeek.MONDAY, true, "09:00", "21:00", "13:00", "14:00"),
                createDayHours(DayOfWeek.TUESDAY, false, null, null, null, null),
                createDayHours(DayOfWeek.WEDNESDAY, true, "10:00", "20:00", "13:00", "14:00")
        );

        try {
            java.lang.reflect.Field field = OperatingHoursResponse.class.getDeclaredField("operatingHours");
            field.setAccessible(true);
            field.set(response, dayHoursList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    private OperatingHoursResponse.DayHours createDayHours(DayOfWeek dayOfWeek, boolean operating,
                                                           String open, String close,
                                                           String breakStart, String breakEnd) {
        OperatingHoursResponse.DayHours dayHours = new OperatingHoursResponse.DayHours();

        try {
            java.lang.reflect.Field dayOfWeekField = OperatingHoursResponse.DayHours.class.getDeclaredField(
                    "dayOfWeek");
            dayOfWeekField.setAccessible(true);
            dayOfWeekField.set(dayHours, dayOfWeek);

            java.lang.reflect.Field operatingField = OperatingHoursResponse.DayHours.class.getDeclaredField(
                    "operating");
            operatingField.setAccessible(true);
            operatingField.set(dayHours, operating);

            if (operating) {
                java.lang.reflect.Field openField = OperatingHoursResponse.DayHours.class.getDeclaredField("open");
                openField.setAccessible(true);
                openField.set(dayHours, LocalTime.parse(open));

                java.lang.reflect.Field closeField = OperatingHoursResponse.DayHours.class.getDeclaredField("close");
                closeField.setAccessible(true);
                closeField.set(dayHours, LocalTime.parse(close));

                OperatingHoursResponse.BreakTime breakTime = new OperatingHoursResponse.BreakTime();
                java.lang.reflect.Field startField = OperatingHoursResponse.BreakTime.class.getDeclaredField("start");
                startField.setAccessible(true);
                startField.set(breakTime, LocalTime.parse(breakStart));

                java.lang.reflect.Field endField = OperatingHoursResponse.BreakTime.class.getDeclaredField("end");
                endField.setAccessible(true);
                endField.set(breakTime, LocalTime.parse(breakEnd));

                java.lang.reflect.Field breakTimeField = OperatingHoursResponse.DayHours.class.getDeclaredField(
                        "breakTime");
                breakTimeField.setAccessible(true);
                breakTimeField.set(dayHours, breakTime);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return dayHours;
    }
}
