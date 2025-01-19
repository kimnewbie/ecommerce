package hhplus.newgeniee.ecommerce.coupon.domain;

import hhplus.newgeniee.ecommerce.user.domain.User;
import hhplus.newgeniee.ecommerce.util.EntityIdSetter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponIssuerTest {

    private CouponIssuer couponIssuer;

    @BeforeEach
    void setUp() {
        couponIssuer = new CouponIssuer();
    }

    @DisplayName("발급 수량이 남은 쿠폰을 발급할 수 있다.")
    @Test
    void issueCoupon() throws Exception {
        // given
        final long userId = 1L;
        final User user = User.builder().build();
        EntityIdSetter.setId(user, userId);

        final long couponId = 1L;
        final int quantity = 10;
        final DiscountType discountType = DiscountType.FIXED;
        final int discountValue = 1000;

        final Coupon coupon = Coupon.builder()
                .quantity(quantity)
                .discountType(discountType)
                .discountValue(discountValue)
                .build();
        EntityIdSetter.setId(coupon, couponId);

        final CouponQuantity couponQuantity = CouponQuantity.builder().couponId(couponId).quantity(quantity).build();

        // when
        final IssuedCoupon result = couponIssuer.issue(user, coupon, couponQuantity);

        // then
        assertThat(result).isNotNull()
                .extracting("couponId", "userId", "discountType", "discountValue")
                .containsExactly(couponId, userId, discountType, discountValue);

        assertThat(coupon.getQuantity()).isEqualTo(couponQuantity.getQuantity());
    }

    @DisplayName("쿠폰 발급 가능 수량을 초과해서 발급요청을 하면 IllegalArgumentException 예외가 발생한다.")
    @Test
    void issueOverLimit() throws Exception {
        // given
        final long couponId = 1L;
        final int quantity = 0;

        final User user = User.builder().build();
        final Coupon coupon = Coupon.builder().quantity(quantity).build();
        final CouponQuantity couponQuantity = CouponQuantity.builder().couponId(couponId).quantity(quantity).build();

        // when & then
        assertThatThrownBy(() -> couponIssuer.issue(user, coupon, couponQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰 수량이 부족합니다.");
    }
}
