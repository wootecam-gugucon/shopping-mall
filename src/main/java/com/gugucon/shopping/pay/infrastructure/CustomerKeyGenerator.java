package com.gugucon.shopping.pay.infrastructure;

import com.gugucon.shopping.member.domain.entity.Member;

public interface CustomerKeyGenerator {

    String generate(Member member);
}
