package com.gugucon.shopping.auth.dto;

import com.gugucon.shopping.member.domain.entity.Member;
import com.gugucon.shopping.member.domain.vo.Email;
import com.gugucon.shopping.member.domain.vo.Gender;
import com.gugucon.shopping.member.domain.vo.Nickname;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberPrincipal {

    private final Long id;
    private final LocalDate birthDate;
    private final Gender gender;
    private final Email email;
    private final Nickname nickname;

    public static MemberPrincipal from(final Member member) {
        return new MemberPrincipal(member.getId(),
                                   member.getBirthDate(),
                                   member.getGender(),
                                   member.getEmail(),
                                   member.getNickname());
    }
}
