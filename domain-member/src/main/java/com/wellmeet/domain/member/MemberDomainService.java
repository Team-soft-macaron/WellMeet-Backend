package com.wellmeet.domain.member;

import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.member.exception.MemberErrorCode;
import com.wellmeet.domain.member.exception.MemberException;
import com.wellmeet.domain.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberDomainService {

    private final MemberRepository memberRepository;

    public Member getById(String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public List<Member> findAllByIds(List<String> memberIds) {
        return memberRepository.findAllByIdIn(memberIds);
    }
}
