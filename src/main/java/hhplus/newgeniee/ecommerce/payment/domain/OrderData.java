package hhplus.newgeniee.ecommerce.payment.domain;

import hhplus.newgeniee.ecommerce.order.domain.Order;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class OrderData {
    private final Long orderId;
    private final Long userId;
    private final int amount;

    public static OrderData from(final Order order) {
        return new OrderData(order.getId(), order.getUserId(), order.getAmount());
    }
}
