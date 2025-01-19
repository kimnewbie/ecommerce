package hhplus.newgeniee.ecommerce.coupon.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IssuedCouponTest {

    @DisplayName("발급된 쿠폰 객체를 생성할 수 있다.")
    @Test
    void createIssuedCoupon() throws Exception {
        // given
        final Long couponId = 1L;
        final Long userId = 1L;
        final DiscountType discountType = DiscountType.FIXED;
        final int discountValue = 1000;
        final LocalDateTime usedAt = LocalDateTime.of(2025, 1, 10, 14, 0);
        final LocalDateTime expiredAt = LocalDateTime.of(2025, 1, 19, 14, 0);

        // when
        final IssuedCoupon result = IssuedCoupon.builder()
                .couponId(couponId)
                .userId(userId)
                .discountType(discountType)
                .discountValue(discountValue)
                .expiredAt(expiredAt)
                .usedAt(usedAt)
                .build();

        // then
        assertThat(result).isNotNull()
                .extracting("couponId", "userId", "discountType", "discountValue", "expiredAt", "usedAt")
                .containsExactly(couponId, userId, discountType, discountValue, expiredAt, usedAt);
    }

    @DisplayName("할인이 없는 빈 발급된 쿠폰 객체를 생성할 수 있다.")
    @Test
    void createEmptyCoupon() throws Exception {
        // when
        final IssuedCoupon result = IssuedCoupon.emptyCoupon();

        // then
        assertThat(result).isNotNull()
                .extracting("couponId", "userId", "discountType", "discountValue", "expiredAt", "usedAt")
                .containsExactly(null, null, DiscountType.NONE, 0, LocalDateTime.MAX, null);
    }

    @DisplayName("할인양이 100이 넘는 정률할인 발급된 쿠폰 객체를 생성하면 IllegalArgumentException 예외가 발생한다.")
    @Test
    void createRateCoupon() throws Exception {
        // given
        final DiscountType discountType = DiscountType.RATE;
        final int discountValue = 101;

        // when & then
        assertThatThrownBy(() -> IssuedCoupon.builder()
                .discountType(discountType)
                .discountValue(discountValue)
                .build()
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("할인율은 100을 초과할 수 없습니다.");
    }

    @DisplayName("발급받은 쿠폰을 사용할 수 있다.")
    @Test
    void validateIssuedCoupon() throws Exception {
        // given
        final LocalDateTime expiredAt = LocalDateTime.of(2025, 1, 10, 14, 0);
        final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
                .expiredAt(expiredAt)
                .usedAt(null)
                .build();

        final Long orderId = 1L;
        final LocalDateTime usedAt = expiredAt.minusMinutes(1);

        // when
        issuedCoupon.use(orderId, usedAt);

        // then
        assertThat(issuedCoupon.getOrderId()).isEqualTo(orderId);
        assertThat(issuedCoupon.getUsedAt()).isEqualTo(usedAt);
    }

    @DisplayName("유효기간이 지난 쿠폰을 사용하려고 하면 IllegalStateException 예외가 발생한다.")
    @Test
    void validateIssuedCouponExpired() throws Exception {
        // given
        final LocalDateTime expiredAt = LocalDateTime.of(2025, 1, 10, 14, 0);
        final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
                .expiredAt(expiredAt)
                .usedAt(null)
                .build();

        final Long orderId = 1L;
        final LocalDateTime usedAt = expiredAt.plusMinutes(1);

        // when * then
        assertThatThrownBy(() -> issuedCoupon.use(orderId, usedAt))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("만료된 쿠폰입니다.");
    }

    @DisplayName("이미 사용한 쿠폰을 사용하려고 하면 IllegalStateException 예외가 발생한다.")
    @Test
    void validateIssuedCouponUsed() throws Exception {
        // given
        // given
        final LocalDateTime expiredAt = LocalDateTime.of(2025, 1, 10, 14, 0);
        final LocalDateTime usedAt = LocalDateTime.of(2025, 1, 10, 14, 0);
        final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
                .expiredAt(expiredAt)
                .usedAt(usedAt)
                .build();

        final Long orderId = 1L;
        final LocalDateTime tryToUsedAt = expiredAt.minusMinutes(1);

        // when * then
        assertThatThrownBy(() -> issuedCoupon.use(orderId, tryToUsedAt))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 사용된 쿠폰입니다.");
    }
}
