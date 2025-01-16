package hhplus.newgeniee.ecommerce.order.infrastructure;

import hhplus.newgeniee.ecommerce.order.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {
}