package com.wellmeet.restaurant.dto;

import com.wellmeet.client.dto.BusinessHourDTO;
import com.wellmeet.common.DayOfWeek;
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
            this.dayOfWeek = DayOfWeek.valueOf(dto.getDayOfWeek());
            this.open = dto.getOpen();
            this.close = dto.getClose();
            this.operating = dto.isOperating();
            this.breakTime = new BreakTime(dto);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class BreakTime {

        private LocalTime start;
        private LocalTime end;

        public BreakTime(BusinessHourDTO dto) {
            this.start = dto.getBreakStart();
            this.end = dto.getBreakEnd();
        }
    }
}
