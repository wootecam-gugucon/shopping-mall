package com.gugucon.shopping.member.repository;

import java.util.Optional;

import com.gugucon.shopping.member.domain.vo.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gugucon.shopping.member.domain.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(Email email);
}
