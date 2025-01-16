package hhplus.newgeniee.ecommerce.order.api.controller;

import hhplus.newgeniee.ecommerce.global.CommonApiResponse;
import hhplus.newgeniee.ecommerce.global.api.ApiSuccessResponse;
import hhplus.newgeniee.ecommerce.order.api.request.OrderCreateApiRequest;
import hhplus.newgeniee.ecommerce.order.api.response.OrderCreateApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "주문 API")
public interface IOrderApiController {

    @ApiSuccessResponse(OrderCreateApiResponse.class)
    @Operation(
            summary = "주문 생성",
            description = "상품 상세에서 주문을 생성한다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = OrderCreateApiRequest.class)
                    )
            )
    )
    @PostMapping
    CommonApiResponse<OrderCreateApiResponse> createOrder(
            @RequestBody OrderCreateApiRequest request
    );
}