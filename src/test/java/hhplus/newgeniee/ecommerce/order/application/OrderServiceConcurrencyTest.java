package hhplus.newgeniee.ecommerce.order.application;

import hhplus.newgeniee.ecommerce.common.ServiceIntegrationTest;
import hhplus.newgeniee.ecommerce.order.api.request.OrderCreateRequest;
import hhplus.newgeniee.ecommerce.order.api.request.OrderItemCreateRequest;
import hhplus.newgeniee.ecommerce.product.domain.Product;
import hhplus.newgeniee.ecommerce.product.domain.Stock;
import hhplus.newgeniee.ecommerce.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class OrderServiceConcurrencyTest extends ServiceIntegrationTest {

    @DisplayName("재고가 10개인 상품에 대해 1개씩 20번의 주문 요청이 동시에 들어오면 10개의 요청만 성공한다.")
    @Test
    void orderOneProduct() throws Exception {
        // given
        final User user = userJpaRepository.save(User.builder().name("사용자").build());
        final Product product = productJpaRepository.save(Product.builder().name("상품A").price(100).build());

        final int quantity = 10;
        stockJpaRepository.save(Stock.builder().productId(product.getId()).quantity(quantity).build());

        final int orderQuantity = 1;
        final List<OrderItemCreateRequest> orderItemCreateRequests = List.of(
                OrderItemCreateRequest.builder()
                        .productId(product.getId())
                        .quantity(orderQuantity)
                        .build()
        );

        final OrderCreateRequest request = OrderCreateRequest.builder()
                .userId(user.getId())
                .orderItems(orderItemCreateRequests)
                .build();

        final int orderTryCount = 20;
        final List<CompletableFuture<Boolean>> tasks = new ArrayList<>(orderTryCount);
        final AtomicInteger exceptionCount = new AtomicInteger(0);

        // when
        for (int i = 1; i <= orderTryCount; i++) {
            tasks.add(CompletableFuture.supplyAsync(() -> {
                orderService.order(request);
                return true;
            }).exceptionally(e -> {
                if (e.getMessage().contains("주문 수량이 현재 재고를 초과할 수 없습니다.")) {
                    exceptionCount.incrementAndGet();
                }
                return false;
            }));
        }

        // then
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();

        int successCount = 0;
        int failureCount = 0;
        for (CompletableFuture<Boolean> task : tasks) {
            if (task.get()) {
                successCount++;
            } else {
                failureCount++;
            }
        }

        assertThat(exceptionCount.get()).isEqualTo(10);
        assertThat(successCount).isEqualTo(10);
        assertThat(failureCount).isEqualTo(exceptionCount.get());
    }

    @DisplayName("재고가 각각 10개, 20개인 상품들에 대해 각 상품당 1개씩 20번의 주문 요청이 동시에 들어오면 10개의 요청만 성공한다.")
    @Test
    void orderManyProduct() throws Exception {
        // given
        final User user = userJpaRepository.save(User.builder().name("사용자").build());
        final Product productA = productJpaRepository.save(Product.builder().name("상품A").price(100).build());
        final Product productB = productJpaRepository.save(Product.builder().name("상품B").price(100).build());

        final int quantityA = 10;
        stockJpaRepository.save(Stock.builder().productId(productA.getId()).quantity(quantityA).build());

        final int quantityB = 20;
        stockJpaRepository.save(Stock.builder().productId(productB.getId()).quantity(quantityB).build());

        final int orderQuantity = 1;
        final List<OrderItemCreateRequest> orderItemCreateRequests = List.of(
                OrderItemCreateRequest.builder()
                        .productId(productA.getId())
                        .quantity(orderQuantity)
                        .build(),
                OrderItemCreateRequest.builder()
                        .productId(productB.getId())
                        .quantity(orderQuantity)
                        .build()
        );

        final OrderCreateRequest request = OrderCreateRequest.builder()
                .userId(user.getId())
                .orderItems(orderItemCreateRequests)
                .build();

        final int orderTryCount = 20;
        final List<CompletableFuture<Boolean>> tasks = new ArrayList<>(orderTryCount);
        final AtomicInteger exceptionCount = new AtomicInteger(0);

        // when
        for (int i = 1; i <= orderTryCount; i++) {
            tasks.add(CompletableFuture.supplyAsync(() -> {
                orderService.order(request);
                return true;
            }).exceptionally(e -> {
                if (e.getMessage().contains("주문 수량이 현재 재고를 초과할 수 없습니다.")) {
                    exceptionCount.incrementAndGet();
                }
                return false;
            }));
        }

        // then
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();

        int successCount = 0;
        int failureCount = 0;
        for (CompletableFuture<Boolean> task : tasks) {
            if (task.get()) {
                successCount++;
            } else {
                failureCount++;
            }
        }

        assertThat(exceptionCount.get()).isEqualTo(10);
        assertThat(successCount).isEqualTo(10);
        assertThat(failureCount).isEqualTo(exceptionCount.get());
    }
}
