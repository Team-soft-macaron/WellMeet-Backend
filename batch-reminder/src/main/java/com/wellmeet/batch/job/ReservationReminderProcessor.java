package com.wellmeet.batch.job;

import com.wellmeet.domain.member.MemberDomainService;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.kafka.dto.payload.ReservationReminderPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationReminderProcessor implements ItemProcessor<Reservation, ReservationReminderPayload> {

    private final MemberDomainService memberDomainService;

    @Override
    public ReservationReminderPayload process(Reservation reservation) {
        log.info("Processing reservation reminder for reservation ID: {}", reservation.getId());

        Member member = memberDomainService.getById(reservation.getMemberId());

        return new ReservationReminderPayload(
                reservation.getId(),
                reservation.getMemberId(),
                member.getName(),
                reservation.getRestaurantName(),
                reservation.getDateTime(),
                reservation.getPartySize()
        );
    }
}
