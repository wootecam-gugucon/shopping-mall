package com.gugucon.shopping.member.domain.vo;

public enum Gender {

    FEMALE,
    MALE;

    public static Gender from(final String gender) {
        return Gender.valueOf(gender.toUpperCase());
    }
}
