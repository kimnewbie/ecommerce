package hhplus.newgeniee.ecommerce.coupon.infrastructure;

import hhplus.newgeniee.ecommerce.coupon.domain.IssuedCoupon;
import hhplus.newgeniee.ecommerce.coupon.domain.IssuedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class IssuedCouponRepositoryImpl implements IssuedCouponRepository {

    private final IssuedCouponJpaRepository issuedCouponJpaRepository;

    @Override
    public IssuedCoupon save(final IssuedCoupon issuedCoupon) {
        return issuedCouponJpaRepository.save(issuedCoupon);
    }

    @Override
    public Optional<IssuedCoupon> findByCouponIdAndUserIdForUpdate(final Long couponId, final Long userId) {
        return issuedCouponJpaRepository.findByCouponIdAndUserIdForUpdate(couponId, userId);
    }
}