package hhplus.newgeniee.ecommerce.coupon.api.response;

import hhplus.newgeniee.ecommerce.coupon.domain.Coupon;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CouponResponse {

    private final long couponId;
    private final String name;
    private final int issueLimit;
    private final int quantity;
    private final String discountType;
    private final int discountValue;

    public static CouponResponse from(final Coupon coupon) {
        return CouponResponse.builder()
                .couponId(coupon.getId())
                .name(coupon.getName())
                .issueLimit(coupon.getIssueLimit())
                .quantity(coupon.getQuantity())
                .discountType(coupon.getDiscountType().name())
                .discountValue(coupon.getDiscountValue())
                .build();
    }
}