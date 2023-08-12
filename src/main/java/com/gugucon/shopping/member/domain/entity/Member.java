package com.gugucon.shopping.member.domain.entity;

import com.gugucon.shopping.common.domain.entity.BaseTimeEntity;
import com.gugucon.shopping.member.domain.vo.Email;
import com.gugucon.shopping.member.domain.vo.Nickname;
import com.gugucon.shopping.member.domain.vo.Password;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private Email email;
    @Embedded
    private Password password;
    @Embedded
    private Nickname nickname;

    public Member(final Long id, final String email, final String password) {
        this.id = id;
        this.email = new Email(email);
        this.password = new Password(password);
    }

    public Member(final String email, final String password) {
        this(null, email, password);
    }
}
