package hhplus.newgeniee.ecommerce.coupon.api.controller;

import hhplus.newgeniee.ecommerce.coupon.api.request.CouponIssueApiRequest;
import hhplus.newgeniee.ecommerce.coupon.api.response.CouponApiResponse;
import hhplus.newgeniee.ecommerce.coupon.api.response.IssuedCouponApiResponse;
import hhplus.newgeniee.ecommerce.coupon.application.CouponService;
import hhplus.newgeniee.ecommerce.global.CommonApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
@RestController
public class CouponApiController implements ICouponApiController {

    private final CouponService couponService;

    @GetMapping("/{couponId}")
    @Override
    public CommonApiResponse<CouponApiResponse> getCoupon(@PathVariable final Long couponId) {
        return CommonApiResponse.ok(CouponApiResponse.from(couponService.getCoupon(couponId)));
    }

    @PostMapping("/issue")
    @Override
    public CommonApiResponse<IssuedCouponApiResponse> issueCoupon(@RequestBody final CouponIssueApiRequest request) {
        return CommonApiResponse.ok(IssuedCouponApiResponse.from(
                couponService.issueCoupon(request.toServiceRequest())
        ));
    }
}
