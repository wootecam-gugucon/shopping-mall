package com.gugucon.shopping.member.domain.entity;

import com.gugucon.shopping.common.domain.entity.BaseTimeEntity;
import com.gugucon.shopping.member.domain.vo.Email;
import com.gugucon.shopping.member.domain.vo.Nickname;
import com.gugucon.shopping.member.domain.vo.Password;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @Valid
    private Email email;

    @Embedded
    @Valid
    private Password password;

    @Embedded
    @Valid
    private Nickname nickname;

    @Builder
    public Member(final Long id, final String email, final String password, final String nickname) {
        this.id = id;
        this.email = Email.from(email);
        this.password = Password.from(password);
        this.nickname = Nickname.from(nickname);
    }
}
