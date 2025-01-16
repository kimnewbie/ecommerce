package hhplus.newgeniee.ecommerce.coupon.infrastructure;

import hhplus.newgeniee.ecommerce.coupon.domain.CouponQuantity;
import hhplus.newgeniee.ecommerce.coupon.domain.CouponQuantityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CouponQuantityRepositoryImpl implements CouponQuantityRepository {

    private final CouponQuantityJpaRepository couponQuantityJpaRepository;

    @Override
    public Optional<CouponQuantity> findByCouponIdForUpdate(final long couponId) {
        return couponQuantityJpaRepository.findByCouponIdForUpdate(couponId);
    }
}
