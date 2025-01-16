package hhplus.newgeniee.ecommerce.product.application;

import hhplus.newgeniee.ecommerce.product.api.request.ProductSearchRequest;
import hhplus.newgeniee.ecommerce.product.api.response.BestProductResponse;
import hhplus.newgeniee.ecommerce.product.api.response.ProductResponse;
import hhplus.newgeniee.ecommerce.product.domain.Product;
import hhplus.newgeniee.ecommerce.product.domain.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductResponse> getProducts(final ProductSearchRequest request, final Pageable pageable) {
        Page<Product> products = productRepository.getProducts(request.toSearchSpec(), pageable);
        return products.map(ProductResponse::from);
    }

    public ProductResponse getProduct(final Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("상품 ID는 1 이상의 값이어야 합니다.");
        }

        final Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));

        return ProductResponse.from(product);
    }

    public List<BestProductResponse> getBestProducts() {
        LocalDateTime endDateTime = LocalDateTime.now();
        LocalDateTime startDateTime = endDateTime.minusDays(3);

        Pageable pageable = Pageable.ofSize(5);

        return productRepository.getBestProducts(startDateTime, endDateTime, pageable)
                .stream()
                .map(BestProductResponse::from)
                .toList();
    }
}
