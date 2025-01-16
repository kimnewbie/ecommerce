package hhplus.newgeniee.ecommerce.coupon.infrastructure;

import hhplus.newgeniee.ecommerce.coupon.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {
}
