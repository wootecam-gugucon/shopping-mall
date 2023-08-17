package com.gugucon.shopping.member.domain.entity;

import com.gugucon.shopping.common.domain.entity.BaseTimeEntity;
import com.gugucon.shopping.member.domain.vo.Email;
import com.gugucon.shopping.member.domain.vo.Nickname;
import com.gugucon.shopping.member.domain.vo.Password;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "members")
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
    public Member(final Long id, final String email, final Password password, final String nickname) {
        this.id = id;
        this.email = Email.from(email);
        this.password = password;
        this.nickname = Nickname.from(nickname);
    }

    public boolean matchPassword(final String password, final PasswordEncoder passwordEncoder) {
        return this.password.hasValue(password, passwordEncoder);
    }
}
