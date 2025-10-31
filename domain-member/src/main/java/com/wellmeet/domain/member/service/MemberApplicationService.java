package com.wellmeet.domain.member.service;

import com.wellmeet.domain.member.MemberDomainService;
import com.wellmeet.domain.member.dto.MemberResponse;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberApplicationService {

    private final MemberDomainService memberDomainService;
    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponse createMember(String name, String nickname, String email, String phone) {
        Member member = new Member(name, nickname, email, phone);
        Member savedMember = memberRepository.save(member);
        return MemberResponse.from(savedMember);
    }

    public MemberResponse getMemberById(String memberId) {
        Member member = memberDomainService.getById(memberId);
        return MemberResponse.from(member);
    }

    public List<MemberResponse> getMembersByIds(List<String> memberIds) {
        return memberDomainService.findAllByIds(memberIds)
                .stream()
                .map(MemberResponse::from)
                .toList();
    }

    @Transactional
    public void deleteMember(String memberId) {
        Member member = memberDomainService.getById(memberId);
        memberRepository.delete(member);
    }
}
