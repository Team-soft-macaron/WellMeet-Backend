package com.wellmeet.domain.member.repository;

import com.wellmeet.domain.member.entity.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

    List<Member> findAllByIdIn(List<String> memberIds);
}
