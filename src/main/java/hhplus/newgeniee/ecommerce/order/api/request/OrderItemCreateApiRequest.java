package hhplus.newgeniee.ecommerce.order.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "주문에 생성될 상품 정보")
@Getter
public class OrderItemCreateApiRequest {

    @Schema(description = "상품 ID", example = "1")
    private final Long productId;

    @Schema(description = "주문 상품 수량", example = "100")
    private final int quantity;

    @Builder
    private OrderItemCreateApiRequest(final Long productId, final int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("주문 수량은 1 이상이어야 합니다.");
        }
        this.productId = productId;
        this.quantity = quantity;
    }

    public OrderItemCreateRequest toServiceRequest() {
        return OrderItemCreateRequest.builder()
                .productId(productId)
                .quantity(quantity)
                .build();
    }
}