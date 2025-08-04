package com.wellmeet.domain.member.entity;

import com.wellmeet.domain.common.BaseEntity;
import com.wellmeet.domain.member.exception.MemberErrorCode;
import com.wellmeet.domain.member.exception.MemberException;
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

    protected static final int MAX_NAME_LENGTH = 10;
    protected static final int MAX_NICKNAME_LENGTH = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String nickname;

    @NotBlank
    private String email;

    @NotBlank
    private String phone;

    private boolean reservationEnabled;
    private boolean remindEnabled;
    private boolean reviewEnabled;
    private boolean isVip;

    public Member(String name, String nickname, String email, String phone) {
        validateName(name);
        validateNickname(nickname);

        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.phone = phone;
        this.reservationEnabled = true;
        this.remindEnabled = true;
        this.reviewEnabled = true;
        this.isVip = false;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank() || name.length() > MAX_NAME_LENGTH) {
            throw new MemberException(MemberErrorCode.MEMBER_NAME_INVALID);
        }
    }

    private void validateNickname(String nickname) {
        if (nickname == null || nickname.isBlank() || nickname.length() > MAX_NICKNAME_LENGTH) {
            throw new MemberException(MemberErrorCode.MEMBER_NICKNAME_INVALID);
        }
    }
}
