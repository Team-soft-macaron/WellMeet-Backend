package com.wellmeet.reservation.dto;

import com.wellmeet.domain.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CustomerSummaryResponse {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private boolean isVip;

    public CustomerSummaryResponse(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.phone = member.getPhone();
        this.email = member.getEmail();
        this.isVip = member.isVip();
    }
}
