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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
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

    public boolean matchPassword(final String rawPassword, final PasswordEncoder passwordEncoder) {
        return this.password.hasValue(rawPassword, passwordEncoder);
    }
}
