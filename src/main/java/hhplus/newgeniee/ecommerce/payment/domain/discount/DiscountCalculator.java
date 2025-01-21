package hhplus.newgeniee.ecommerce.payment.domain.discount;

import hhplus.newgeniee.ecommerce.coupon.domain.IssuedCoupon;
import hhplus.newgeniee.ecommerce.order.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DiscountCalculator {

    private final DiscountPolicyFactory discountPolicyFactory;

    @Cacheable(cacheNames = "discounts", key = "#order.id + '-' + #issuedCoupon.id")
    public int calculateDiscountAmount(final Order order, final IssuedCoupon issuedCoupon) {
        return discountPolicyFactory.getDiscountPolicy(issuedCoupon.getDiscountType())
                .calculateDiscountAmount(order.getAmount(), issuedCoupon.getDiscountValue());
    }
}
