package com.gugucon.shopping.member.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Getter
public class Nickname {

    @Column(name = "nickname", nullable = false)
    @NotNull
    private String value;

    public static Nickname from(String value) {
        return new Nickname(value);
    }
}
