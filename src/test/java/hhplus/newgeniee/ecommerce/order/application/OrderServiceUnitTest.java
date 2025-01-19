package hhplus.newgeniee.ecommerce.order.application;


import hhplus.newgeniee.ecommerce.order.api.request.OrderCreateRequest;
import hhplus.newgeniee.ecommerce.order.api.request.OrderItemCreateRequest;
import hhplus.newgeniee.ecommerce.order.api.response.OrderCreateResponse;
import hhplus.newgeniee.ecommerce.order.domain.Order;
import hhplus.newgeniee.ecommerce.order.domain.OrderItem;
import hhplus.newgeniee.ecommerce.order.domain.OrderItemRepository;
import hhplus.newgeniee.ecommerce.order.domain.OrderRepository;
import hhplus.newgeniee.ecommerce.product.domain.*;
import hhplus.newgeniee.ecommerce.user.domain.User;
import hhplus.newgeniee.ecommerce.user.domain.UserRepository;
import hhplus.newgeniee.ecommerce.util.EntityIdSetter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
class OrderServiceUnitTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private StockRepository stockRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StockService stockService;

    @DisplayName("여러 종류의 상품과 주문 수량을 받아서 주문을 생성할 수 있다.")
    @Test
    void order() throws Exception {
        // given
        final Long userId = 1L;
        final User user = User.builder()
                .name("사용자")
                .build();

        EntityIdSetter.setId(user, userId);
        given(userRepository.existsById(userId))
                .willReturn(true);

        final Long productIdA = 1L;
        final int priceA = 100;
        final int quantityA = 10;
        final Product productA = Product.builder()
                .name("상품A")
                .price(priceA)
                .build();

        final Long productIdB = 2L;
        final int priceB = 200;
        final int quantityB = 20;
        final Product productB = Product.builder()
                .name("상품B")
                .price(priceB)
                .build();

        EntityIdSetter.setId(productA, productIdA);
        EntityIdSetter.setId(productB, productIdB);
        given(productRepository.getAllById(List.of(productIdA, productIdB)))
                .willReturn(List.of(productA, productB));

        final Stock stockA = Stock.builder()
                .productId(productIdA)
                .quantity(quantityA)
                .build();
        given(stockRepository.findByProductIdForUpdate(productIdA))
                .willReturn(Optional.of(stockA));

        final Stock stockB = Stock.builder()
                .productId(productIdB)
                .quantity(quantityB)
                .build();
        given(stockRepository.findByProductIdForUpdate(productIdB))
                .willReturn(Optional.of(stockB));

        final Long orderId = 1L;
        final Order order = Order.builder()
                .userId(userId)
                .build();

        EntityIdSetter.setId(order, orderId);
        given(orderRepository.save(any(Order.class)))
                .willReturn(order);

        final OrderItem orderItemA = OrderItem.builder()
                .orderId(order.getId())
                .productId(productA.getId())
                .productName(productA.getName())
                .quantity(quantityA)
                .price(productA.getPrice())
                .build();
        given(orderItemRepository.save(argThat(item ->
                item != null && item.getProductId().equals(productA.getId())
        ))).willReturn(orderItemA);

        final OrderItem orderItemB = OrderItem.builder()
                .orderId(order.getId())
                .productId(productB.getId())
                .productName(productB.getName())
                .quantity(quantityB)
                .price(productB.getPrice())
                .build();
        given(orderItemRepository.save(argThat(item ->
                item != null && item.getProductId().equals(productB.getId())
        ))).willReturn(orderItemB);

        final List<OrderItemCreateRequest> orderItemCreateRequests = List.of(
                OrderItemCreateRequest.builder()
                        .productId(productA.getId())
                        .quantity(quantityA)
                        .build(),
                OrderItemCreateRequest.builder()
                        .productId(productB.getId())
                        .quantity(quantityB)
                        .build()
        );

        final OrderCreateRequest orderCreateRequest = OrderCreateRequest.builder()
                .userId(user.getId())
                .orderItems(orderItemCreateRequests)
                .build();

        final int expectedAmount = (priceA * quantityA) + (priceB * quantityB);

        // when
        final OrderCreateResponse result = orderService.order(orderCreateRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isNotNull();
        assertThat(result.getAmount()).isEqualTo(expectedAmount);
    }

    @DisplayName("존재하지 않는 사용자로 주문을 생성하려고 하면 NoSuchElementException 예외가 발생한다.")
    @Test
    void orderWithInvalidUser() throws Exception {
        // given
        final Long userId = 1L;
        given(userRepository.existsById(userId))
                .willReturn(false);

        final OrderCreateRequest orderCreateRequest = OrderCreateRequest.builder()
                .userId(userId)
                .build();

        // when & then
        assertThatThrownBy(() -> orderService.order(orderCreateRequest))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("존재하지 않는 상품으로 주문을 생성하려고 하면 NoSuchElementException 예외가 발생한다.")
    @Test
    void orderWithInvalidProduct() throws Exception {
        // given
        final Long userId = 1L;
        final User user = User.builder()
                .name("사용자")
                .build();

        EntityIdSetter.setId(user, userId);
        given(userRepository.existsById(userId))
                .willReturn(true);

        final Long productId = 1L;

        final OrderCreateRequest orderCreateRequest = OrderCreateRequest.builder()
                .userId(user.getId())
                .orderItems(List.of(
                        OrderItemCreateRequest.builder()
                                .productId(productId)
                                .quantity(10)
                                .build()
                ))
                .build();

        // when & then
        assertThatThrownBy(() -> orderService.order(orderCreateRequest))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("상품을 찾을 수 없습니다.");
    }

    @DisplayName("주문 생성시 상품 재고가 부족하면 IllegalArgumentException 예외가 발생한다.")
    @Test
    void orderWithQuantity() throws Exception {
        // given
        final Long userId = 1L;
        final User user = User.builder()
                .name("사용자")
                .build();

        EntityIdSetter.setId(user, userId);
        given(userRepository.existsById(userId))
                .willReturn(true);

        final Long productId = 1L;
        final int price = 100;
        final int quantity = 10;
        final Product product = Product.builder()
                .name("상품A")
                .price(price)
                .build();

        EntityIdSetter.setId(product, productId);
        given(productRepository.getAllById(List.of(productId)))
                .willReturn(List.of(product));

        final Stock stock = Stock.builder()
                .productId(productId)
                .quantity(quantity)
                .build();
        given(stockRepository.findByProductIdForUpdate(productId))
                .willReturn(Optional.of(stock));

        final int orderQuantity = stock.getQuantity() + 1;

        final OrderCreateRequest orderCreateRequest = OrderCreateRequest.builder()
                .userId(user.getId())
                .orderItems(List.of(
                        OrderItemCreateRequest.builder()
                                .productId(product.getId())
                                .quantity(orderQuantity)
                                .build()
                ))
                .build();

        willThrow(new IllegalArgumentException("주문 수량이 현재 재고를 초과할 수 없습니다."))
                .given(stockService)
                .decrease(product, stock, orderQuantity);

        // when & then
        assertThatThrownBy(() -> orderService.order(orderCreateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주문 수량이 현재 재고를 초과할 수 없습니다.");
    }
}
