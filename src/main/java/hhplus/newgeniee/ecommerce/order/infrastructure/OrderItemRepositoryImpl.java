package hhplus.newgeniee.ecommerce.order.infrastructure;

import hhplus.newgeniee.ecommerce.order.domain.OrderItem;
import hhplus.newgeniee.ecommerce.order.domain.OrderItemRepository;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public OrderItem save(final OrderItem orderItem) {
        return orderItemJpaRepository.save(orderItem);
    }
}
