package hhplus.newgeniee.ecommerce.order.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Schema(description = "주문 생성 요청")
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class OrderCreateApiRequest {

    @Schema(description = "사용자 ID", example = "1")
    private final Long userId;

    @Schema(description = "주문 상품 목록")
    private final List<OrderItemCreateApiRequest> orderItems;

    public OrderCreateRequest toServiceRequest() {
        return OrderCreateRequest.builder()
                .userId(userId)
                .orderItems(
                        orderItems.stream()
                                .map(OrderItemCreateApiRequest::toServiceRequest)
                                .toList()
                )
                .build();
    }
}
