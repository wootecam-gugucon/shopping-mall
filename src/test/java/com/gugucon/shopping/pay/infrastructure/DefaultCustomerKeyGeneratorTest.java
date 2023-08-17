package com.gugucon.shopping.pay.infrastructure;

import com.gugucon.shopping.TestUtils;
import com.gugucon.shopping.member.domain.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DefaultCustomerKeyGeneratorTest 단위 테스트")
class DefaultCustomerKeyGeneratorTest {

    private final CustomerKeyGenerator customerKeyGenerator = new DefaultCustomerKeyGenerator();

    @Test
    @DisplayName("같은 회원에 대해 같은 키를 생성한다.")
    void generate_sameMember() {
        // given
        final Member member = TestUtils.createMember();

        // when
        final String keyA = customerKeyGenerator.generate(member);
        final String keyB = customerKeyGenerator.generate(member);

        // then
        assertThat(keyA).isEqualTo(keyB);
    }
}
