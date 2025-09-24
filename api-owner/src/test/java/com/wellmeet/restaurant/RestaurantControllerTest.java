package com.wellmeet.restaurant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wellmeet.BaseControllerTest;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.restaurant.businesshour.entity.DayOfWeek;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.restaurant.dto.OperatingHoursResponse;
import com.wellmeet.restaurant.dto.UpdateOperatingHoursRequest;
import com.wellmeet.restaurant.dto.UpdateRestaurantRequest;
import com.wellmeet.restaurant.dto.UpdateRestaurantResponse;
import com.wellmeet.restaurant.dto.UpdateOperatingHoursRequest.DayHours;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RestaurantControllerTest extends BaseControllerTest {

    @Nested
    class GetOperatingHours {

        @Test
        void 식당의_운영시간을_불러온다() {
            Owner owner = ownerGenerator.generate("owner1");
            Restaurant restaurant = restaurantGenerator.generate("restaurant1", owner);
            businessHourGenerator.generate(DayOfWeek.TUESDAY, restaurant);
            businessHourGenerator.generate(DayOfWeek.WEDNESDAY, restaurant);
            businessHourGenerator.generate(DayOfWeek.MONDAY, restaurant);

            OperatingHoursResponse operatingHoursResponse = given().pathParam("restaurantId", restaurant.getId())
                    .queryParam("ownerId", owner.getId()).when().get("/owner/restaurant/{restaurantId}/operating-hours")
                    .then().statusCode(200).extract().as(OperatingHoursResponse.class);

            assertAll(() -> assertThat(operatingHoursResponse.getOperatingHours()).hasSize(3),
                    () -> assertThat(operatingHoursResponse.getOperatingHours().get(0).getDayOfWeek()).isEqualTo(
                            DayOfWeek.MONDAY),
                    () -> assertThat(operatingHoursResponse.getOperatingHours().get(1).getDayOfWeek()).isEqualTo(
                            DayOfWeek.TUESDAY),
                    () -> assertThat(operatingHoursResponse.getOperatingHours().get(2).getDayOfWeek()).isEqualTo(
                            DayOfWeek.WEDNESDAY));
        }
    }

    @Nested
    class UpdateOperatingHours {

        @Test
        void 식당의_운영시간을_업데이트한다() {
            Owner owner = ownerGenerator.generate("owner1");
            Restaurant restaurant = restaurantGenerator.generate("restaurant1", owner);
            businessHourGenerator.generate(DayOfWeek.TUESDAY, restaurant);
            businessHourGenerator.generate(DayOfWeek.WEDNESDAY, restaurant);
            businessHourGenerator.generate(DayOfWeek.MONDAY, restaurant);
            UpdateOperatingHoursRequest request = new UpdateOperatingHoursRequest(
                    List.of(new DayHours(DayOfWeek.MONDAY, true, LocalTime.of(9, 0), LocalTime.of(21, 0),
                                    LocalTime.of(13, 0), LocalTime.of(14, 0)),
                            new DayHours(DayOfWeek.TUESDAY, false, null, null, null, null),
                            new DayHours(DayOfWeek.WEDNESDAY, true, LocalTime.of(10, 0), LocalTime.of(20, 0),
                                    LocalTime.of(13, 0), LocalTime.of(14, 0))));

            OperatingHoursResponse operatingHoursResponse = given().contentType("application/json")
                    .pathParam("restaurantId", restaurant.getId()).queryParam("ownerId", owner.getId()).body(request)
                    .when().put("/owner/restaurant/{restaurantId}/operating-hours").then().statusCode(200).extract()
                    .as(OperatingHoursResponse.class);

            assertAll(() -> assertThat(operatingHoursResponse.getOperatingHours()).hasSize(3),
                    () -> assertThat(operatingHoursResponse.getOperatingHours().get(0).getDayOfWeek()).isEqualTo(
                            DayOfWeek.MONDAY),
                    () -> assertThat(operatingHoursResponse.getOperatingHours().get(0).isOperating()).isTrue(),
                    () -> assertThat(operatingHoursResponse.getOperatingHours().get(0).getOpen()).isEqualTo("09:00"),
                    () -> assertThat(operatingHoursResponse.getOperatingHours().get(0).getClose()).isEqualTo("21:00"),
                    () -> assertThat(
                            operatingHoursResponse.getOperatingHours().get(0).getBreakTime().getStart()).isEqualTo(
                            "13:00"), () -> assertThat(
                            operatingHoursResponse.getOperatingHours().get(0).getBreakTime().getEnd()).isEqualTo(
                            "14:00"),
                    () -> assertThat(operatingHoursResponse.getOperatingHours().get(1).getDayOfWeek()).isEqualTo(
                            DayOfWeek.TUESDAY),
                    () -> assertThat(operatingHoursResponse.getOperatingHours().get(1).isOperating()).isFalse(),
                    () -> assertThat(operatingHoursResponse.getOperatingHours().get(2).getDayOfWeek()).isEqualTo(
                            DayOfWeek.WEDNESDAY),
                    () -> assertThat(operatingHoursResponse.getOperatingHours().get(2).isOperating()).isTrue(),
                    () -> assertThat(operatingHoursResponse.getOperatingHours().get(2).getOpen()).isEqualTo("10:00"),
                    () -> assertThat(operatingHoursResponse.getOperatingHours().get(2).getClose()).isEqualTo("20:00"),
                    () -> assertThat(
                            operatingHoursResponse.getOperatingHours().get(2).getBreakTime().getStart()).isEqualTo(
                            "13:00"), () -> assertThat(
                            operatingHoursResponse.getOperatingHours().get(2).getBreakTime().getEnd()).isEqualTo(
                            "14:00"));
        }
    }

    @Nested
    class UpdateRestaurant{

        @Test
        void 식당_정보를_갱신한다(){
            Owner owner = ownerGenerator.generate("owner1");
            Restaurant restaurant = restaurantGenerator.generate("restaurant1", owner);
            String newRestaurantName = "new restaurant";
            UpdateRestaurantRequest request = new UpdateRestaurantRequest(newRestaurantName, "address", 36.5, 128.0, "thumbnail");
            UpdateRestaurantResponse response = given().contentType("application/json")
                .pathParam("restaurantId", restaurant.getId()).queryParam("ownerId", owner.getId()).body(request)
                .when().put("/owner/restaurant/{restaurantId}").then().statusCode(200).extract().as(UpdateRestaurantResponse.class);
            assertThat(response.getName()).isEqualTo(newRestaurantName);
        }
    }
}
