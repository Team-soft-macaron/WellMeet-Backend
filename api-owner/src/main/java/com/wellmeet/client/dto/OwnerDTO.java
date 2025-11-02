package com.wellmeet.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerDTO {

    private String id;
    private String name;
    private String email;
    private boolean reservationEnabled;
    private boolean reviewEnabled;
}
