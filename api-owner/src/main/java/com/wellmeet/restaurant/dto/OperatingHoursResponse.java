package com.wellmeet.restaurant.dto;

import com.wellmeet.domain.restaurant.businesshour.entity.BusinessHour;
import com.wellmeet.domain.restaurant.businesshour.entity.BusinessHours;
import com.wellmeet.domain.restaurant.businesshour.entity.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OperatingHoursResponse {

    private List<DayHours> operatingHours;

    public OperatingHoursResponse(BusinessHours operatingHours) {
        this.operatingHours = operatingHours.getValue()
                .stream()
                .map(DayHours::new)
                .toList();
    }

    @Getter
    @NoArgsConstructor
    public static class DayHours {

        private DayOfWeek dayOfWeek;
        private LocalTime open;
        private LocalTime close;
        private boolean operating;
        private BreakTime breakTime;

        public DayHours(BusinessHour businessHour) {
            this.dayOfWeek = businessHour.getDayOfWeek();
            this.open = businessHour.getOpenTime();
            this.close = businessHour.getCloseTime();
            this.operating = businessHour.isOpen();
            this.breakTime = new BreakTime(businessHour);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class BreakTime {

        private LocalTime start;
        private LocalTime end;

        public BreakTime(BusinessHour businessHour) {
            this.start = businessHour.getBreakStartTime();
            this.end = businessHour.getBreakEndTime();
        }
    }
}
