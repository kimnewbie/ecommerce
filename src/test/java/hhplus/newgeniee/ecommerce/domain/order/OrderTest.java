package hhplus.newgeniee.ecommerce.domain.order;

import hhplus.newgeniee.ecommerce.application.service.product.ProductService;
import hhplus.newgeniee.ecommerce.domain.product.Product;
import hhplus.newgeniee.ecommerce.domain.product.ProductRepository;
import hhplus.newgeniee.ecommerce.domain.product.ProductStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void testFindByNameContainingIgnoreCase() {
        // Arrange: 테스트 데이터를 준비
        Product product1 = Product.builder()
                .name("Apple iPhone 14")
                .price(BigDecimal.valueOf(999.99))
                .stockQty(50)
                .totalSalesQty(200)
                .status(ProductStatus.SELLING)
                .build();

        Product product2 = Product.builder()
                .name("Samsung Galaxy S22")
                .price(BigDecimal.valueOf(799.99))
                .stockQty(30)
                .totalSalesQty(150)
                .status(ProductStatus.SELLING)
                .build();

        when(productRepository.findByNameContainingIgnoreCase("iPhone")).thenReturn(List.of(product1));

        // Act: 서비스를 호출
        List<Product> result = productService.findByNameContainingIgnoreCase("iPhone");

        // Assert: 결과 확인
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Apple iPhone 14");
    }

}