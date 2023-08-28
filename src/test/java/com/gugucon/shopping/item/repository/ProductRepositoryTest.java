package com.gugucon.shopping.item.repository;

import static com.gugucon.shopping.utils.DomainUtils.createMemberWithoutId;
import static com.gugucon.shopping.utils.DomainUtils.createOrderItem;
import static com.gugucon.shopping.utils.DomainUtils.createOrderWithoutId;
import static org.assertj.core.api.Assertions.assertThat;

import com.gugucon.shopping.common.config.JpaConfig;
import com.gugucon.shopping.common.domain.vo.Quantity;
import com.gugucon.shopping.item.domain.entity.OrderStat;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.member.domain.entity.Member;
import com.gugucon.shopping.member.domain.vo.Gender;
import com.gugucon.shopping.member.repository.MemberRepository;
import com.gugucon.shopping.order.domain.PayType;
import com.gugucon.shopping.order.domain.entity.Order;
import com.gugucon.shopping.order.domain.entity.Order.OrderStatus;
import com.gugucon.shopping.order.domain.entity.OrderItem;
import com.gugucon.shopping.order.repository.OrderItemRepository;
import com.gugucon.shopping.order.repository.OrderRepository;
import com.gugucon.shopping.utils.DomainUtils;
import com.gugucon.shopping.utils.StatsUtils;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Import(JpaConfig.class)
@DataJpaTest
@DisplayName("ProductRepository 단위 테스트")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderStatRepository orderStatRepository;

    @Test
    @DisplayName("해당 키워드를 이름에 포함하는 product를 주문이 많은 순으로 조회한다.")
    void findAllByNameSortByOrderCountDesc() {
        // given
        final Product 사과 = insertProduct("사과", 2500);   // O
        final Product 맛있는사과 = insertProduct("맛있는 사과", 3000);    // O
        final Product 사과는맛있어 = insertProduct("사과는 맛있어", 1000);    // O
        final Product 가나다라마사과과 = insertProduct("가나다라마사과과", 4000);    // O
        final Product 가나다라마바사 = insertProduct("가나다라마바사", 2000);    // X

        final Member member = createMemberWithoutId("test@email.com", LocalDate.now(), Gender.FEMALE);
        final Member persistMember = memberRepository.save(member);

        final Order order = createOrderWithoutId(persistMember.getId(), OrderStatus.COMPLETED, PayType.NONE);
        orderRepository.save(order);

        final OrderItem 사과_주문상품 = createOrderItem("사과", 사과.getId(), Quantity.from(10));
        final OrderItem 사과는맛있어_주문상품 = createOrderItem("사과는 맛있어", 사과는맛있어.getId(), Quantity.from(9));
        final OrderItem 가나다라마사과과_주문상품 = createOrderItem("가나다라마사과과", 가나다라마사과과.getId(), Quantity.from(8));
        final OrderItem 맛있는사과_주문상품 = createOrderItem("맛있는 사과", 맛있는사과.getId(), Quantity.from(7));
        final OrderItem 가나다라마바사_주문상품 = createOrderItem("가나다라마바사", 가나다라마바사.getId(), Quantity.from(10));

        final List<OrderItem> orderItems = List.of(
                사과_주문상품, 맛있는사과_주문상품, 사과는맛있어_주문상품, 가나다라마사과과_주문상품, 가나다라마바사_주문상품
        );
        orderItemRepository.saveAll(orderItems);
        insertOrderStats(member, orderItems);

        final String keyword = "사과";

        // when
        final Page<Product> products = productRepository.findAllByNameSortByOrderCountDesc(keyword, Pageable.ofSize(20));

        // then
        assertThat(products.getContent()).containsExactly(사과, 사과는맛있어, 가나다라마사과과, 맛있는사과);
    }

    private Product insertProduct(final String productName, final long price) {
        final Product product = DomainUtils.createProductWithoutId(productName, price, 10);
        return productRepository.save(product);
    }

    private void insertOrderStats(final Member member, final List<OrderItem> orderItems) {
        final List<OrderStat> orderStats = StatsUtils.createSingleOrderStat(member, orderItems);
        orderStatRepository.saveAll(orderStats);
    }
}
