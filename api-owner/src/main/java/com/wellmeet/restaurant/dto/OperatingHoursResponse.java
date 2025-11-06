package com.wellmeet.restaurant.dto;

import com.wellmeet.common.dto.BusinessHourDTO;
import com.wellmeet.reservation.dto.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OperatingHoursResponse {

    private List<DayHours> operatingHours;

    public OperatingHoursResponse(List<BusinessHourDTO> businessHours) {
        this.operatingHours = businessHours.stream()
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

        public DayHours(BusinessHourDTO dto) {
            this.dayOfWeek = DayOfWeek.valueOf(dto.dayOfWeek().name());
            this.open = dto.openTime();
            this.close = dto.closeTime();
            this.operating = dto.isOpen();
            this.breakTime = new BreakTime(dto);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class BreakTime {

        private LocalTime start;
        private LocalTime end;

        public BreakTime(BusinessHourDTO dto) {
            this.start = dto.breakStartTime();
            this.end = dto.breakEndTime();
        }
    }
}
