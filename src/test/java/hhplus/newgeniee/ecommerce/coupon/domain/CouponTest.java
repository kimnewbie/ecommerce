package hhplus.newgeniee.ecommerce.coupon.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponTest {

    @DisplayName("쿠폰 객체를 생성할 수 있다.")
    @Test
    void createCoupon() throws Exception {
        // given
        final String name = "쿠폰1";
        final int issueLimit = 30;
        final int quantity = 30;
        final DiscountType discountType = DiscountType.FIXED;
        final int discountValue = 1000;
        final LocalDateTime expiredAt = LocalDateTime.of(2025, 1, 9, 14, 00);

        // when
        final Coupon result = Coupon.builder()
                .name(name)
                .issueLimit(issueLimit)
                .quantity(quantity)
                .discountType(discountType)
                .discountValue(discountValue)
                .expiredAt(expiredAt)
                .build();

        // then
        assertThat(result).isNotNull()
                .extracting("name", "issueLimit", "quantity", "discountValue", "expiredAt")
                .containsExactly(name, issueLimit, quantity, discountValue, expiredAt);
    }

    @DisplayName("할인양이 100이 넘는 정률할인 쿠폰 객체를 생성하면 IllegalArgumentException 예외가 발생한다.")
    @Test
    void createRateCoupon() throws Exception {
        // given
        final DiscountType discountType = DiscountType.RATE;
        final int discountValue = 101;

        // when & then
        assertThatThrownBy(() -> Coupon.builder()
                .discountType(discountType)
                .discountValue(discountValue)
                .build()
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("할인율은 100을 초과할 수 없습니다.");
    }

    @DisplayName("쿠폰의 남은 발급 수량을 수정할 수 있다.")
    @Test
    void updateQuantity() throws Exception {
        final Coupon coupon = Coupon.builder()
                .quantity(30)
                .build();

        final int updateQuantity = 29;

        // when
        coupon.updateQuantity(updateQuantity);

        // then
        assertThat(coupon.getQuantity()).isEqualTo(updateQuantity);
    }
}
