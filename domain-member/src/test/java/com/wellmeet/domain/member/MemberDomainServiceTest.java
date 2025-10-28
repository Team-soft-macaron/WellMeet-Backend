package com.wellmeet.domain.member;

import static org.assertj.core.api.Assertions.*;

import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.member.exception.MemberException;
import com.wellmeet.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import(MemberDomainService.class)
class MemberDomainServiceTest extends BaseRepositoryTest {

    @Autowired
    private MemberDomainService memberDomainService;

    @Autowired
    private MemberRepository memberRepository;

    @Nested
    class GetById {

        @Test
        void 회원을_조회한다() {
            Member member = createAndSaveMember("testMember");

            Member result = memberDomainService.getById(member.getId());

            assertThat(result.getId()).isEqualTo(member.getId());
            assertThat(result.getName()).isEqualTo("testMember");
        }

        @Test
        void 존재하지_않는_회원_조회_시_예외가_발생한다() {
            String nonExistentId = "non-existent-id";

            assertThatThrownBy(() -> memberDomainService.getById(nonExistentId))
                    .isInstanceOf(MemberException.class);
        }
    }

    private Member createAndSaveMember(String name) {
        Member member = new Member(name, "nickname", "email@example.com", "010-1234-5678");
        return memberRepository.save(member);
    }
}
