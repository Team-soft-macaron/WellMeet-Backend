package com.wellmeet.domain.member;

import com.wellmeet.domain.exception.DomainErrorCode;
import com.wellmeet.domain.exception.WellMeetDomainException;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberDomainService {

    private final MemberRepository memberRepository;

    public Member getById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new WellMeetDomainException(DomainErrorCode.MEMBER_NOT_FOUND));
    }

    /**
     * N+1 문제 해결을 위한 일괄 조회 메서드
     * 여러 Member ID를 한번에 조회하여 Map으로 반환
     */
    public Map<Long, Member> getAllByIds(Set<Long> memberIds) {
        List<Member> members = memberRepository.findAllById(memberIds);
        return members.stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));
    }
}
