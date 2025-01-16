package hhplus.newgeniee.ecommerce.coupon.api.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CouponIssueRequest {
    private final long userId;
    private final long couponId;
}