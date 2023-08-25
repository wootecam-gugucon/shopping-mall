package com.gugucon.shopping.auth.dto;

import com.gugucon.shopping.member.domain.entity.Member;
import com.gugucon.shopping.member.domain.vo.Gender;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberPrincipal {

    private Long id;
    private LocalDate birthDate;
    private Gender gender;

    public static MemberPrincipal from(Member member) {
        return new MemberPrincipal(member.getId(), member.getBirthDate(), member.getGender());
    }
}