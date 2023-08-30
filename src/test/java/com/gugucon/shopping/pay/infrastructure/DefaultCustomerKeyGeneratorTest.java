package com.gugucon.shopping.pay.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.gugucon.shopping.member.domain.entity.Member;
import com.gugucon.shopping.utils.DomainUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DefaultCustomerKeyGeneratorTest 단위 테스트")
class DefaultCustomerKeyGeneratorTest {

    private final CustomerKeyGenerator customerKeyGenerator = new DefaultCustomerKeyGenerator();

    @Test
    @DisplayName("같은 회원에 대해 같은 키를 생성한다.")
    void generate_sameMember() {
        // given
        final Member member = DomainUtils.createMember();

        // when
        final String keyA = customerKeyGenerator.generate(member.getId());
        final String keyB = customerKeyGenerator.generate(member.getId());

        // then
        assertThat(keyA).isEqualTo(keyB);
    }
}
