package hhplus.newgeniee.ecommerce.coupon.domain;

import java.util.Optional;

public interface CouponQuantityRepository {

    Optional<CouponQuantity>  findByCouponIdForUpdate(long couponId);
}