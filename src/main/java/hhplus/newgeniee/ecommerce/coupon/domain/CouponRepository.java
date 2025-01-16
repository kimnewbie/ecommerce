package hhplus.newgeniee.ecommerce.coupon.domain;

import java.util.Optional;

public interface CouponRepository {

    Optional<Coupon> findById(long couponId);
}