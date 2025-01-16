package hhplus.newgeniee.ecommerce.payment.api.controller;

import hhplus.newgeniee.ecommerce.global.CommonApiResponse;
import hhplus.newgeniee.ecommerce.global.api.ApiFailResponse;
import hhplus.newgeniee.ecommerce.global.api.ApiSuccessResponse;
import hhplus.newgeniee.ecommerce.payment.api.request.PaymentApiRequest;
import hhplus.newgeniee.ecommerce.payment.api.response.PaymentApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "결제 API")
public interface IPaymentApiController {

    @ApiFailResponse("보유 포인트가 부족합니다.")
    @ApiSuccessResponse(PaymentApiResponse.class)
    @Operation(
            summary = "결제",
            description = "주문에 대한 결제를 한다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = PaymentApiRequest.class)
                    )
            )
    )
    @PostMapping
    CommonApiResponse<PaymentApiResponse> payment(@RequestBody PaymentApiRequest request);
}
