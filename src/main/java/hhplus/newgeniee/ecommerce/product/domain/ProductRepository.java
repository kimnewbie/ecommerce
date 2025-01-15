package hhplus.newgeniee.ecommerce.product.domain;

import hhplus.newgeniee.ecommerce.product.domain.spec.ProductSearchSpec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface ProductRepository {

    Page<Product> getProducts(ProductSearchSpec spec, Pageable pageable);

    Optional<Product> findById(long id);

    List<Product> getAllById(List<Long> productIds);

    List<BestProduct> getBestProducts(LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);
}
