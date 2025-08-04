package com.wellmeet.domain.member.exception;

import com.wellmeet.domain.common.WellMeetDomainException;
import lombok.Getter;

@Getter
public class MemberException extends WellMeetDomainException {

    public MemberException(MemberErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode.getStatusCode());
    }
}
