package hhplus.newgeniee.ecommerce.coupon.api.response;

import hhplus.newgeniee.ecommerce.coupon.domain.Coupon;
import hhplus.newgeniee.ecommerce.coupon.domain.IssuedCoupon;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class IssuedCouponResponse {

    private final long couponId;
    private final String name;
    private final String discountType;
    private final int discountValue;
    private final LocalDateTime expiredAt;

    public static IssuedCouponResponse of(final Coupon coupon, final IssuedCoupon issuedCoupon) {
        return IssuedCouponResponse.builder()
                .couponId(coupon.getId())
                .name(coupon.getName())
                .discountType(issuedCoupon.getDiscountType().name())
                .discountValue(issuedCoupon.getDiscountValue())
                .expiredAt(issuedCoupon.getExpiredAt())
                .build();
    }
}