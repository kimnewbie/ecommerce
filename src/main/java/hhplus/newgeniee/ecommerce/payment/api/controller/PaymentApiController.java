package hhplus.newgeniee.ecommerce.payment.api.controller;

import hhplus.newgeniee.ecommerce.global.CommonApiResponse;
import hhplus.newgeniee.ecommerce.payment.api.request.PaymentApiRequest;
import hhplus.newgeniee.ecommerce.payment.api.response.PaymentApiResponse;
import hhplus.newgeniee.ecommerce.payment.api.response.PaymentResponse;
import hhplus.newgeniee.ecommerce.payment.application.PaymentApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
@RestController
public class PaymentApiController implements IPaymentApiController {

    private final PaymentApplicationService paymentApplicationService;

    @PostMapping
    @Override
    public CommonApiResponse<PaymentApiResponse> payment(@RequestBody final PaymentApiRequest request) {
        final PaymentResponse paymentResponse = paymentApplicationService.pay(request.toServiceRequest());
        return CommonApiResponse.ok(PaymentApiResponse.from(paymentResponse));
    }

}
