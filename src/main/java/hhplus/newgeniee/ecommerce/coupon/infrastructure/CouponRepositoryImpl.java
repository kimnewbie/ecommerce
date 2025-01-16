package hhplus.newgeniee.ecommerce.coupon.infrastructure;

import hhplus.newgeniee.ecommerce.coupon.domain.Coupon;
import hhplus.newgeniee.ecommerce.coupon.domain.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;

    @Override
    public Optional<Coupon> findById(final long couponId) {
        return couponJpaRepository.findById(couponId);
    }
}