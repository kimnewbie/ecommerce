package hhplus.newgeniee.ecommerce.product.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockServiceTest {

    private StockService stockService;

    @BeforeEach
    void setUp() {
        stockService = new StockService();
    }

    @DisplayName("주문으로 인한 상품 재고 차감에 성공하면 주문으로 인한 재고 변경 이력을 반환한다.")
    @Test
    void decrease() throws Exception {
        // given
        final int beforeQuantity = 10;
        final int decreaseQuantity = 4;
        final int expectedQuantity = 6;

        final Product product = Product.builder().build();
        final Stock stock = Stock.builder()
                .productId(product.getId())
                .quantity(beforeQuantity) // 최초 10개의 재고
                .build();

        // when
        stockService.decrease(product, stock, decreaseQuantity);

        // then
        assertThat(stock.getQuantity()).isEqualTo(expectedQuantity);
        assertThat(product.getQuantity()).isEqualTo(stock.getQuantity());
    }

    @DisplayName("상품 재고를 초과해서 주문 재고 차감 요청을 하면 IllegalArgumentException  예외가 발생한다.")
    @Test
    void whenOderExceedsStock() throws Exception {
        // given
        final int beforeQuantity = 10;
        final int decreaseQuantity = 11;

        Product product = Product.builder().build();
        Stock stock = Stock.builder()
                .productId(product.getId())
                .quantity(beforeQuantity)
                .build();

        // when & then
        assertThatThrownBy(()->stockService
                .decrease(product, stock, decreaseQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문 수량이 현재 재고를 초과할 수 없습니다.");
    }
}