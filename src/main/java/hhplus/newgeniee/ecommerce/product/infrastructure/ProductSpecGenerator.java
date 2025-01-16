package hhplus.newgeniee.ecommerce.product.infrastructure;

import hhplus.newgeniee.ecommerce.product.domain.Product;
import hhplus.newgeniee.ecommerce.product.domain.spec.ProductSearchSpec;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

@Component
public class ProductSpecGenerator {

    public Specification<Product> searchWith(final ProductSearchSpec spec) {
        return ((root, query, builder) -> {
            final ArrayList<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(spec.getName())) {
                predicates.add(builder.like(root.get("name"), "%" + spec.getName() + "%"));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
