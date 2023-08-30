package com.gugucon.shopping.member.domain.entity;

import com.gugucon.shopping.common.domain.entity.BaseTimeEntity;
import com.gugucon.shopping.member.domain.vo.Email;
import com.gugucon.shopping.member.domain.vo.Gender;
import com.gugucon.shopping.member.domain.vo.Nickname;
import com.gugucon.shopping.member.domain.vo.Password;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

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

    @NotNull
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    @NotNull
    private LocalDate birthDate;

    public boolean matchPassword(final String rawPassword, final PasswordEncoder passwordEncoder) {
        return this.password.hasValue(rawPassword, passwordEncoder);
    }
}
