package hhplus.newgeniee.ecommerce.coupon.application;

import hhplus.newgeniee.ecommerce.coupon.api.request.CouponIssueRequest;
import hhplus.newgeniee.ecommerce.coupon.api.response.CouponResponse;
import hhplus.newgeniee.ecommerce.coupon.api.response.IssuedCouponResponse;
import hhplus.newgeniee.ecommerce.coupon.domain.*;
import hhplus.newgeniee.ecommerce.user.domain.User;
import hhplus.newgeniee.ecommerce.user.domain.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponQuantityRepository couponQuantityRepository;
    private final IssuedCouponRepository issuedCouponRepository;
    private final UserRepository userRepository;
    private final CouponIssuer couponIssuer;

    public CouponResponse getCoupon(final Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("쿠폰 ID는 0보다 큰 값이어야 합니다.");
        }

        final Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("쿠폰을 찾을 수 없습니다."));
        return CouponResponse.from(coupon);
    }

    @Transactional
    public IssuedCouponResponse issueCoupon(final CouponIssueRequest request) {

        final User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        final Coupon coupon = couponRepository.findById(request.getCouponId())
                .orElseThrow(() -> new EntityNotFoundException("쿠폰을 찾을 수 없습니다."));

        final CouponQuantity couponQuantity = couponQuantityRepository.findByCouponIdForUpdate(request.getCouponId())
                .orElseThrow(() -> new EntityNotFoundException("쿠폰 수량 정보를 찾을 수 없습니다."));

        issuedCouponRepository.findByCouponIdAndUserIdForUpdate(coupon.getId(), user.getId())
                .ifPresent(issuedCoupon -> {
                    throw new IllegalStateException("이미 발급된 쿠폰입니다.");
                });

        final IssuedCoupon issuedCoupon = couponIssuer.issue(user, coupon, couponQuantity);
        issuedCouponRepository.save(issuedCoupon);

        return IssuedCouponResponse.of(coupon, issuedCoupon);
    }
}
