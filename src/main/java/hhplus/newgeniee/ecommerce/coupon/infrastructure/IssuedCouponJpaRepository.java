package hhplus.newgeniee.ecommerce.coupon.infrastructure;

import hhplus.newgeniee.ecommerce.coupon.domain.IssuedCoupon;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface IssuedCouponJpaRepository extends JpaRepository<IssuedCoupon, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from IssuedCoupon i where i.couponId = :couponId and i.userId = :userId")
    Optional<IssuedCoupon> findByCouponIdAndUserIdForUpdate(Long couponId, Long userId);
}
