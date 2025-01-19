package hhplus.newgeniee.ecommerce.order.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @DisplayName("주문 객체를 생성할 수 있다.")
    @Test
    void createOrder() throws Exception {
        // given
        final Long userId = 1L;
        final int amount = 1000;
        final OrderStatus status = OrderStatus.ORDERED;

        // when
        final Order result = Order.builder()
                .userId(userId)
                .amount(amount)
                .status(status)
                .build();

        // then
        assertThat(result).isNotNull()
                .extracting("userId", "amount", "status")
                .containsExactly(userId, amount, status);
    }
}