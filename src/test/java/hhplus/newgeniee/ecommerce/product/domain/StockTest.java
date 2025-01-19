package hhplus.newgeniee.ecommerce.product.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockTest {

    @DisplayName("상품 재고 객체를 생성할 수 있다.")
    @Test
    void createStock() throws Exception {
        // given
        final int quantity = 10;
        Product product = Product.builder().build();

        // when
        final Stock stock = Stock.builder()
                .productId(product.getId())
                .quantity(quantity)
                .build();

        // then
        assertThat(stock.getQuantity()).isEqualTo(quantity);
        assertThat(stock.getProductId()).isEqualTo(product.getId());
    }

    @DisplayName("상품 재고를 차감할 수 있다.")
    @Test
    void decreaseQuantity() throws Exception {
        // given
        final int quantity = 10;
        final int decreaseQuantity = 4;
        final int expectedQuantity = 6;

        Product product = Product.builder().build();
        Stock stock = Stock.builder()
                .productId(product.getId())
                .quantity(quantity)
                .build();

        // when
        stock.decrease(decreaseQuantity);

        // then
        assertThat(stock.getQuantity()).isEqualTo(expectedQuantity);
    }

    @DisplayName("상품 재고를 초과해서 주문 재고 차감 요청을 하면 IllegalArgumentException  예외가 발생한다.")
    @Test
    void whenOderExceedsStock() throws Exception {
        // given
        final int quantity = 3;
        final int decreaseQuantity = 10;

        Product product = Product.builder().build();
        Stock stock = Stock.builder()
                .productId(product.getId())
                .quantity(quantity)
                .build();


        // when & then
        assertThatThrownBy(() -> stock
                .decrease(decreaseQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문 수량이 현재 재고를 초과할 수 없습니다.");
    }

}