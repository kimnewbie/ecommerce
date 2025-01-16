package hhplus.newgeniee.ecommerce.order.api.response;

import hhplus.newgeniee.ecommerce.order.domain.Order;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class OrderCreateResponse {

    private final Long orderId;
    private final int amount;

    public static OrderCreateResponse from(final Order order) {
        return OrderCreateResponse.builder()
                .orderId(order.getId())
                .amount(order.getAmount())
                .build();
    }
}