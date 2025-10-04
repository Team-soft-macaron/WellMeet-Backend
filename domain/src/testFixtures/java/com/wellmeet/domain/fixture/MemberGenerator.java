package com.wellmeet.domain.fixture;

import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Component;

@Component
public class MemberGenerator {

    private final MemberRepository memberRepository;

    public MemberGenerator(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member generate(String name) {
        Member member = new Member(name, "nickname", name + "@example.com", "phone");
        return memberRepository.save(member);
    }
}
