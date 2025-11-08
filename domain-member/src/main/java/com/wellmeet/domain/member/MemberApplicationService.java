package com.wellmeet.domain.member;

import com.wellmeet.common.dto.MemberDTO;
import com.wellmeet.domain.member.domainservice.MemberDomainService;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberApplicationService {

    private final MemberDomainService memberDomainService;
    private final MemberRepository memberRepository;

    @Transactional
    public MemberDTO createMember(String name, String nickname, String email, String phone) {
        Member member = new Member(name, nickname, email, phone);
        Member savedMember = memberRepository.save(member);
        return toDTO(savedMember);
    }

    public MemberDTO getMemberById(String memberId) {
        Member member = memberDomainService.getById(memberId);
        return toDTO(member);
    }

    public List<MemberDTO> getMembersByIds(List<String> memberIds) {
        return memberDomainService.findAllByIds(memberIds)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public void deleteMember(String memberId) {
        Member member = memberDomainService.getById(memberId);
        memberRepository.delete(member);
    }

    private MemberDTO toDTO(Member member) {
        return new MemberDTO(
                member.getId(),
                member.getName(),
                member.getNickname(),
                member.getEmail(),
                member.getPhone(),
                member.isReservationEnabled(),
                member.isRemindEnabled(),
                member.isReviewEnabled(),
                member.isVip(),
                member.getCreatedAt(),
                member.getUpdatedAt()
        );
    }
}
