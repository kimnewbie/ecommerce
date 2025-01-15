package hhplus.newgeniee.ecommerce.product.api.request;

import hhplus.newgeniee.ecommerce.product.domain.spec.ProductSearchSpec;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ProductSearchRequest {
    private final String name;

    public ProductSearchSpec toSearchSpec() {
        return ProductSearchSpec.builder()
                .name(name)
                .build();
    }
}
