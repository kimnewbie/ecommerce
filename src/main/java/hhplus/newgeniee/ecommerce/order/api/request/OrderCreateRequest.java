package hhplus.newgeniee.ecommerce.order.api.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class OrderCreateRequest {

    private final Long userId;
    private final List<OrderItemCreateRequest> orderItems;

    public List<Long> extractProductIds() {
        return orderItems.stream()
                .map(OrderItemCreateRequest::getProductId)
                .toList();
    }
}