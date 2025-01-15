package hhplus.newgeniee.ecommerce.product.infrastructure;

import hhplus.newgeniee.ecommerce.product.domain.Stock;
import hhplus.newgeniee.ecommerce.product.domain.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class StockRepositoryImpl implements StockRepository {

    private final StockJpaRepository stockJpaRepository;

    @Override
    public Optional<Stock> findByProductIdForUpdate(final long productId) {
        return stockJpaRepository.findByProductIdForUpdate(productId);
    }
}