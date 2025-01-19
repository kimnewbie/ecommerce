package hhplus.newgeniee.ecommerce.payment.domain;

import hhplus.newgeniee.ecommerce.coupon.domain.DiscountType;
import hhplus.newgeniee.ecommerce.coupon.domain.IssuedCoupon;
import hhplus.newgeniee.ecommerce.order.domain.Order;
import hhplus.newgeniee.ecommerce.order.domain.OrderStatus;
import hhplus.newgeniee.ecommerce.payment.application.PaymentService;
import hhplus.newgeniee.ecommerce.payment.domain.discount.*;
import hhplus.newgeniee.ecommerce.point.domain.Point;
import hhplus.newgeniee.ecommerce.util.EntityIdSetter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentServiceTest {

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        final DiscountPolicyFactory discountPolicyFactory = new DiscountPolicyFactory(
                new NoneDiscountPolicy(),
                new FixedDiscountPolicy(),
                new RateDiscountPolicy()
        );
        final DiscountCalculator discountCalculator = new DiscountCalculator(discountPolicyFactory);

        paymentService = new PaymentService(discountCalculator);
    }

    @DisplayName("쿠폰을 적용하지 않으면 주문 총 금액을 결제한다.")
    @Test
    void payWithNoCoupon() throws Exception {
        // given
        final Long orderId = 1L;
        final int amount = 1000;
        final Order order = Order.builder()
                .amount(amount)
                .status(OrderStatus.ORDERED)
                .build();
        EntityIdSetter.setId(order, orderId);

        final int userPoint = 10000;
        final Point point = Point.builder()
                .point(userPoint)
                .build();

        final IssuedCoupon issuedCoupon = IssuedCoupon.emptyCoupon();

        final int paymentAmount = amount;
        final int expectedPointHeld = userPoint - paymentAmount;

        // when
        final Payment result = paymentService.pay(order, point, issuedCoupon);

        // then
        assertThat(result.getAmount()).isEqualTo(paymentAmount);
        assertThat(point.getPoint()).isEqualTo(expectedPointHeld);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);

        assertThat(issuedCoupon.getOrderId()).isEqualTo(orderId);
        assertThat(issuedCoupon.getUsedAt()).isNotNull();
    }

    @DisplayName("정액할인 쿠폰으로 결제하면 쿠폰에 명시된 할인양만큼 차감된 금액으로 결제한다.")
    @Test
    void payWithFixedCoupon() throws Exception {
        // given
        final Long orderId = 1L;
        final int amount = 1000;
        final Order order = Order.builder()
                .amount(amount)
                .status(OrderStatus.ORDERED)
                .build();
        EntityIdSetter.setId(order, orderId);

        final int userPoint = 10000;
        final Point point = Point.builder()
                .point(userPoint)
                .build();

        final DiscountType discountType = DiscountType.FIXED;
        final int discountValue = 1000;
        final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
                .discountType(discountType)
                .discountValue(discountValue)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .usedAt(null)
                .build();

        final int paymentAmount = amount - discountValue;
        final int expectedPointHeld = userPoint - paymentAmount;

        // when
        final Payment result = paymentService.pay(order, point, issuedCoupon);

        // then
        assertThat(result.getAmount()).isEqualTo(paymentAmount);
        assertThat(point.getPoint()).isEqualTo(expectedPointHeld);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);

        assertThat(issuedCoupon.getOrderId()).isEqualTo(orderId);
        assertThat(issuedCoupon.getUsedAt()).isNotNull();
    }

    @DisplayName("정률할인 쿠폰으로 결제하면 쿠폰에 명시된 할인비율만큼 차감된 금액으로 결제한다.")
    @Test
    void payWithRateCoupon() throws Exception {
        // given
        final Long orderId = 1L;
        final int amount = 10000;
        final Order order = Order.builder()
                .amount(amount)
                .status(OrderStatus.ORDERED)
                .build();
        EntityIdSetter.setId(order, orderId);

        final int userPoint = 10000;
        final Point point = Point.builder()
                .point(userPoint)
                .build();

        final DiscountType discountType = DiscountType.RATE;
        final int discountValue = 15;
        final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
                .discountType(discountType)
                .discountValue(discountValue)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .usedAt(null)
                .build();

        final int paymentAmount = 8500;
        final int expectedPointHeld = userPoint - paymentAmount;

        // when
        final Payment result = paymentService.pay(order, point, issuedCoupon);

        // then
        assertThat(result.getAmount()).isEqualTo(paymentAmount);
        assertThat(point.getPoint()).isEqualTo(expectedPointHeld);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);

        assertThat(issuedCoupon.getOrderId()).isEqualTo(orderId);
        assertThat(issuedCoupon.getUsedAt()).isNotNull();
    }

    @DisplayName("만료된 쿠폰으로 결제를 시도하면 IllegalStateException 예외가 발생한다.")
    @Test
    void payWithExpiredCoupon() throws Exception {
        // given
        final int amount = 1000;
        final Order order = Order.builder()
                .amount(amount)
                .status(OrderStatus.ORDERED)
                .build();

        final int userPoint = 10000;
        final Point point = Point.builder()
                .point(userPoint)
                .build();

        final int discountValue = 100;
        final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
                .discountType(DiscountType.FIXED)
                .discountValue(discountValue)
                .expiredAt(LocalDateTime.now().minusMinutes(1))
                .usedAt(null)
                .build();

        // when & then
        assertThatThrownBy(() -> paymentService.pay(order, point, issuedCoupon))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("만료된 쿠폰입니다.");
    }

    @DisplayName("이미 사용된 쿠폰으로 결제를 시도하면 IllegalStateException 예외가 발생한다.")
    @Test
    void payWithUsedCoupon() throws Exception {
        // given
        final int amount = 1000;
        final Order order = Order.builder()
                .amount(amount)
                .status(OrderStatus.ORDERED)
                .build();

        final int userPoint = 10000;
        final Point point = Point.builder()
                .point(userPoint)
                .build();

        final int discountValue = 100;
        final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
                .discountType(DiscountType.FIXED)
                .discountValue(discountValue)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .usedAt(LocalDateTime.now().minusMinutes(1))
                .build();

        // when & then
        assertThatThrownBy(() -> paymentService.pay(order, point, issuedCoupon))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 사용된 쿠폰입니다.");
    }

    @DisplayName("결제 금액이 보유 포인트보다 많으면 IllegalStateException 예외가 발생한다.")
    @Test
    void payWithNotEnoughPoint() throws Exception {
        // given
        final int amount = 1000;
        final Order order = Order.builder()
                .amount(amount)
                .status(OrderStatus.ORDERED)
                .build();

        final int userPoint = 0;
        final Point point = Point.builder()
                .point(userPoint)
                .build();

        final int discountValue = 100;
        final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
                .discountType(DiscountType.FIXED)
                .discountValue(discountValue)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .usedAt(null)
                .build();

        // when & then
        assertThatThrownBy(() -> paymentService.pay(order, point, issuedCoupon))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("포인트가 부족합니다.");
    }

    @DisplayName("이미 결제한 주문을 결제시도하면 IllegalStateException  예외가 발생한다.")
    @Test
    void payWithAlreadyOrder() throws Exception {
        // given
        final int amount = 1000;
        final Order order = Order.builder()
                .amount(amount)
                .status(OrderStatus.PAID)
                .build();

        final int userPoint = 10000;
        final Point point = Point.builder()
                .point(userPoint)
                .build();

        final int discountValue = 100;
        final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
                .discountType(DiscountType.FIXED)
                .discountValue(discountValue)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .usedAt(null)
                .build();

        // when & then
        assertThatThrownBy(() -> paymentService.pay(order, point, issuedCoupon))
                .isInstanceOf(IllegalStateException .class)
                .hasMessage("이미 결제된 주문입니다.");
    }
}