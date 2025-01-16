package hhplus.newgeniee.ecommerce.coupon.infrastructure;

import hhplus.newgeniee.ecommerce.coupon.domain.CouponQuantity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CouponQuantityJpaRepository extends JpaRepository<CouponQuantity, Long> {

    Optional<CouponQuantity> findByCouponId(long couponId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CouponQuantity c where c.couponId = :couponId")
    Optional<CouponQuantity> findByCouponIdForUpdate(long couponId);
}
