package com.wellmeet.domain.member.entity;

import com.wellmeet.domain.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String nickname;

    @NotBlank
    private String email;

    private boolean reservationEnabled;
    private boolean remindEnabled;
    private boolean reviewEnabled;

    public Member(String name, String nickname, String email) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.reservationEnabled = true;
        this.remindEnabled = true;
        this.reviewEnabled = true;
    }
}
