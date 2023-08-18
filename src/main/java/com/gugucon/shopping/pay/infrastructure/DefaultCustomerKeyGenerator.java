package com.gugucon.shopping.pay.infrastructure;

import com.gugucon.shopping.member.domain.entity.Member;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DefaultCustomerKeyGenerator implements CustomerKeyGenerator {

    @Override
    public String generate(final Member member) {
        return UUID.nameUUIDFromBytes(String.valueOf(member.getId()).getBytes()).toString();
    }
}
