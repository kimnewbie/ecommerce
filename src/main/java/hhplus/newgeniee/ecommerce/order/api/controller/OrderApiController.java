package hhplus.newgeniee.ecommerce.order.api.controller;

import hhplus.newgeniee.ecommerce.global.CommonApiResponse;
import hhplus.newgeniee.ecommerce.order.api.request.OrderCreateApiRequest;
import hhplus.newgeniee.ecommerce.order.api.response.OrderCreateApiResponse;
import hhplus.newgeniee.ecommerce.order.api.response.OrderCreateResponse;
import hhplus.newgeniee.ecommerce.order.application.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@RestController
public class OrderApiController implements IOrderApiController {

    private final OrderService orderService;

    @PostMapping
    @Override
    public CommonApiResponse<OrderCreateApiResponse> createOrder(@RequestBody final OrderCreateApiRequest request) {
        final OrderCreateResponse response = orderService.order(request.toServiceRequest());
        return CommonApiResponse.ok(OrderCreateApiResponse.from(response));
    }
}