package com.wellmeet.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {

    private String id;
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private boolean reservationEnabled;
    private boolean remindEnabled;
    private boolean reviewEnabled;
    private boolean isVip;
}
