package hhplus.newgeniee.ecommerce.order.infrastructure;

import hhplus.newgeniee.ecommerce.order.domain.Order;
import hhplus.newgeniee.ecommerce.order.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(final Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Optional<Order> findByIdForUpdate(final Long id) {
        return orderJpaRepository.findByIdForUpdate(id);
    }
}
