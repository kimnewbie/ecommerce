package hhplus.newgeniee.ecommerce.product.application;

import hhplus.newgeniee.ecommerce.product.api.request.ProductSearchRequest;
import hhplus.newgeniee.ecommerce.product.api.response.ProductResponse;
import hhplus.newgeniee.ecommerce.product.domain.Product;
import hhplus.newgeniee.ecommerce.product.domain.ProductRepository;
import hhplus.newgeniee.ecommerce.product.domain.spec.ProductSearchSpec;
import hhplus.newgeniee.ecommerce.util.EntityIdSetter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceUnitTest {

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
            final long id = 1L;
            final String name = "상품1";
            final int price = 1000;
            final int quantity = 10;

            final ProductSearchRequest request = ProductSearchRequest.builder()
                    .name(name)
                    .build();

            final Pageable pageable = PageRequest.of(0, 10);

            final List<Product> content = List.of(
                    Product.builder()
                            .id(id)
                            .name(name)
                            .price(price)
                            .quantity(quantity)
                            .build()
            );

            given(productRepository.getProducts(any(ProductSearchSpec.class), any(Pageable.class)))
                    .willReturn(new PageImpl<>(content, pageable, 1));

            // when
            final Page<ProductResponse> result = productService.getProducts(request, pageable);

            // then
            assertThat(result.getContent()).hasSize(1)
                    .extracting("id", "name", "price", "quantity")
                    .containsExactly(
                            tuple(id, name, price, quantity)
                    );
            assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
            assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        }

        @DisplayName("조회 조건이 포함된 상품명의 상품 목록을 조회할 수 있다.")
        @Test
        void getProductByNameLike() throws Exception {
            // given
            final String name = "상품";
            final long productIdA = 1L;
            final long productIdB = 2L;

            final ProductSearchRequest request = ProductSearchRequest.builder()
                    .name(name)
                    .build();

            final Pageable pageable = PageRequest.of(0, 10);

            final Product productA = Product.builder().name(name + "1").price(1000).quantity(10).build();
            final Product productB = Product.builder().name(name + "2").price(2000).quantity(20).build();

            EntityIdSetter.setId(productA, productIdA);
            EntityIdSetter.setId(productB, productIdB);

            given(productRepository.getProducts(any(ProductSearchSpec.class), any(Pageable.class)))
                    .willReturn(new PageImpl<>(List.of(productA, productB), pageable, 1));

            // when
            final Page<ProductResponse> result = productService.getProducts(request, pageable);

            // then
            assertThat(result.getContent()).hasSize(2)
                    .extracting("id", "name", "price", "quantity")
                    .containsExactlyInAnyOrder(
                            tuple(productIdA, name + "1", 1000, 10),
                            tuple(productIdB, name + "2", 2000, 20)
                    );
            assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
            assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        }

        @DisplayName("조회 조건이 포함된 상품명을 가진 상품이 없으면 빈 리스트를 반환한다.")
        @Test
        void getProductsByNameNotMatch() throws Exception {
            // given
            final String name = "상품";

            final ProductSearchRequest request = ProductSearchRequest.builder()
                    .name(name)
                    .build();

            final Pageable pageable = PageRequest.of(0, 10);

            final List<Product> content = List.of();

            given(productRepository.getProducts(any(ProductSearchSpec.class), any(Pageable.class)))
                    .willReturn(new PageImpl<>(content, pageable, 1));

            // when
            final Page<ProductResponse> result = productService.getProducts(request, pageable);

            // then
            assertThat(result.getContent()).hasSize(0);
            assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
            assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        }
    }

    @DisplayName("상품 단건 조회")
    @Nested
    class GetSingleProductsNest {
        @DisplayName("상품 아이디로 상품을 조회할 수 있다.")
        @Test
        void getProductById() {
            // given
            final long productId = 1L;
            final String name = "상품1";
            final int price = 1000;
            final int quantity = 10;

            final Product product = Product.builder()
                    .name(name)
                    .price(price)
                    .quantity(quantity)
                    .build();

            EntityIdSetter.setId(product, productId);
            given(productRepository.findById(productId))
                    .willReturn(Optional.of(product));

            // when
            final ProductResponse result = productService.getProduct(productId);

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

        @DisplayName("조회하려는 상품 아이디가 양수가 아니면 IllegalArgumentException 발생한다.")
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

            given(productRepository.findById(id))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productService.getProduct(id))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("상품을 찾을 수 없습니다.");
        }
    }
}