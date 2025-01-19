package hhplus.newgeniee.ecommerce.product.application;

import hhplus.newgeniee.ecommerce.common.ServiceIntegrationTest;
import hhplus.newgeniee.ecommerce.order.domain.Order;
import hhplus.newgeniee.ecommerce.order.domain.OrderItem;
import hhplus.newgeniee.ecommerce.product.api.request.ProductSearchRequest;
import hhplus.newgeniee.ecommerce.product.api.response.BestProductResponse;
import hhplus.newgeniee.ecommerce.product.api.response.ProductResponse;
import hhplus.newgeniee.ecommerce.product.domain.Product;
import hhplus.newgeniee.ecommerce.product.domain.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

class ProductServiceIntegrateTest extends ServiceIntegrationTest {
    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @DisplayName("상품 목록 조회")
    @Nested
    class GetProductsNest {

        @DisplayName("상품명으로 상품 목록을 조회할 수 있다.")
        @Test
        void getProductsByName() {
            // given
            final String name = "상품1";
            final int price = 1000;
            final int quantity = 10;

            productJpaRepository.saveAll(List.of(
                    Product.builder().name(name).price(price).quantity(quantity).build(),
                    Product.builder().name("상품2").price(2000).quantity(20).build(),
                    Product.builder().name("상품3").price(3000).quantity(30).build()
            ));

            final ProductSearchRequest request = ProductSearchRequest.builder()
                    .name(name)
                    .build();

            final Pageable pageable = PageRequest.of(0, 10);

            // when
            final List<ProductResponse> result = productService.getProducts(request, pageable)
                    .getContent();

            // then
            assertThat(result).hasSize(1)
                    .extracting("name", "price", "quantity")
                    .containsExactly(
                            tuple(name, price, quantity)
                    );
        }

        @DisplayName("조회 조건이 포함된 상품명의 상품 목록을 조회할 수 있다.")
        @Test
        void getProductsByNameLike() {
            // given
            final String name = "상품";

            productJpaRepository.saveAll(List.of(
                    Product.builder().name(name + "1").price(1000).quantity(10).build(),
                    Product.builder().name(name + "2").price(2000).quantity(20).build(),
                    Product.builder().name("품상").price(3000).quantity(30).build()
            ));

            final ProductSearchRequest request = ProductSearchRequest.builder()
                    .name(name)
                    .build();

            final Pageable pageable = PageRequest.of(0, 10);

            // when
            final List<ProductResponse> result = productService.getProducts(request, pageable)
                    .getContent();

            // then
            assertThat(result).hasSize(2)
                    .extracting("name", "price", "quantity")
                    .containsExactly(
                            tuple("상품1", 1000, 10),
                            tuple("상품2", 2000, 20)
                    );
        }

        @DisplayName("조회 조건이 포함된 상품명을 가진 상품이 없으면 빈 리스트를 반환한다.")
        @Test
        void getProductsByNameNotMatch() {
            // given
            final String name = "상품";

            productJpaRepository.saveAll(List.of(
                    Product.builder().name("1").price(1000).quantity(10).build(),
                    Product.builder().name("2").price(2000).quantity(20).build(),
                    Product.builder().name("3").price(3000).quantity(30).build()
            ));

            final ProductSearchRequest request = ProductSearchRequest.builder()
                    .name(name)
                    .build();

            final Pageable pageable = PageRequest.of(0, 10);

            // when
            final List<ProductResponse> result = productService.getProducts(request, pageable)
                    .getContent();

            // then
            assertThat(result).hasSize(0);
        }

    }

    @DisplayName("상품 단건 조회")
    @Nested
    class GetSingleProductsNest {
        @DisplayName("상품 아이디로 상품을 조회할 수 있다.")
        @Test
        void getProductById() {
            // given
            final String name = "상품1";
            final int price = 1000;
            final int quantity = 10;

            final Product savedProduct = productJpaRepository.save(
                    Product.builder()
                            .name(name)
                            .price(price)
                            .quantity(quantity)
                            .build()
            );

            // when
            final ProductResponse result = productService.getProduct(savedProduct.getId());

            // then
            assertThat(result).isNotNull()
                    .extracting("name", "price", "quantity")
                    .containsExactly(name, price, quantity);
        }

        @DisplayName("조회하려는 상품 아이디가 Null이면 IllegalArgumentException 발생한다.")
        @Test
        void getProductByNullId() {
            // given
            final Long id = null;

            // when & then
            assertThatThrownBy(() -> productService.getProduct(id))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("상품 ID는 1 이상의 값이어야 합니다.");
        }

        @DisplayName("조회하려는 상품 아이디가 0이면 IllegalArgumentException 발생한다.")
        @Test
        void getProductByZeroId() {
            // given
            final Long id = 0L;

            // when & then
            assertThatThrownBy(() -> productService.getProduct(id))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("상품 ID는 1 이상의 값이어야 합니다.");
        }

        @DisplayName("조회하려는 상품 아이디가 음수면 InvalidRequestException이 발생한다.")
        @Test
        void getProductByNegativeId() {
            // given
            final Long id = -1L;

            // when & then
            assertThatThrownBy(() -> productService.getProduct(id))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("상품 ID는 1 이상의 값이어야 합니다.");
        }

        @DisplayName("상품 아이디로 조회할 때 상품이 존재하지 않으면 NoSuchElementException 발생한다.")
        @Test
        void getProductByIdNotFound() {
            // given
            final Long id = 1L;

            // when & then
            assertThatThrownBy(() -> productService.getProduct(id))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("상품을 찾을 수 없습니다.");
        }
    }

    @DisplayName("인기 상품 조회")
    @Nested
    class GetBestProductsNest {
        @DisplayName("판매량이 가장 많은 상품 5개를 조회할 수 있다.")
        @Test
        void getBestProducts() {
            // given
            final Long userId = 1L;

            List<Long> orderQuantities = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                final Product product = productJpaRepository.save(
                        Product.builder().name(String.valueOf(i)).price(1000).quantity(i * 10).build()
                );

                final Order order = orderJpaRepository.save(Order.createOrder(userId));
                final OrderItem orderItem = orderItemJpaRepository.save(
                        OrderItem.builder()
                                .orderId(order.getId())
                                .productId(product.getId())
                                .productName(product.getName())
                                .price(1000)
                                .quantity(i)
                                .build()
                );
                order.addOrderItem(orderItem);
                orderJpaRepository.save(order);

                orderQuantities.add((long)i);
            }
            orderJpaRepository.flush();

            // when
            final List<BestProductResponse> result = productService.getBestProducts();

            // then
            final List<Long> expectedTop5TotalSaleCount = orderQuantities.stream()
                    .sorted(Comparator.reverseOrder())
                    .limit(5)
                    .toList();

            assertThat(result).hasSize(5)
                    .extracting("totalSaleCount")
                    .containsExactlyElementsOf(expectedTop5TotalSaleCount);
        }
    }
}