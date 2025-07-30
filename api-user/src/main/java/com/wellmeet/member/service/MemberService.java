package com.wellmeet.member.service;

import com.wellmeet.domain.member.domain.Member;
import com.wellmeet.domain.member.repository.MemberRepository;
import com.wellmeet.exception.ErrorCode;
import com.wellmeet.exception.WellMeetException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member getById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new WellMeetException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
