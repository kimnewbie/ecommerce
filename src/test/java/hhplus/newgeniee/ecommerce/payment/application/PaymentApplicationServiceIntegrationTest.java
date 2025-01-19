package hhplus.newgeniee.ecommerce.payment.application;

import hhplus.newgeniee.ecommerce.common.ServiceIntegrationTest;
import hhplus.newgeniee.ecommerce.coupon.domain.DiscountType;
import hhplus.newgeniee.ecommerce.coupon.domain.IssuedCoupon;
import hhplus.newgeniee.ecommerce.order.domain.Order;
import hhplus.newgeniee.ecommerce.order.domain.OrderStatus;
import hhplus.newgeniee.ecommerce.payment.api.request.PaymentRequest;
import hhplus.newgeniee.ecommerce.payment.api.response.PaymentResponse;
import hhplus.newgeniee.ecommerce.point.domain.Point;
import hhplus.newgeniee.ecommerce.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentApplicationServiceIntegrationTest extends ServiceIntegrationTest {

    @DisplayName("정액할인 쿠폰으로 결제하면 쿠폰에 명시된 할인양만큼 차감된 금액으로 결제한다.")
    @Test
    void paymentWithFixedCoupon() throws Exception {
        // given
        final User user = userJpaRepository.save(User.builder().name("사용자").build());

        final Long userId = user.getId();
        final int userPoint = 10000;
        pointJpaRepository.save(Point.builder().userId(userId).point(userPoint).build());

        final int orderAmount = 1000;
        final Order order = orderJpaRepository.save(
                Order.builder()
                        .userId(userId)
                        .amount(orderAmount)
                        .status(OrderStatus.ORDERED)
                        .build()
        );

        final long couponId = 1L;
        final DiscountType discountType = DiscountType.FIXED;
        final int discountValue = 1000;
        final IssuedCoupon issuedCoupon = issuedCouponJpaRepository.save(
                IssuedCoupon.builder()
                        .userId(userId)
                        .couponId(couponId)
                        .discountType(discountType)
                        .discountValue(discountValue)
                        .expiredAt(LocalDateTime.now().plusDays(10))
                        .build()
        );

        final int expectedPaymentPrice = orderAmount - discountValue;
        final int expectedUserPoint = userPoint - expectedPaymentPrice;

        final PaymentRequest request = PaymentRequest.builder()
                .orderId(order.getId())
                .userId(userId)
                .couponId(couponId)
                .build();

        // when
        final PaymentResponse result = paymentApplicationService.pay(request);

        // then
        assertThat(result).isNotNull()
                .extracting("orderId", "paymentPrice")
                .containsExactly(order.getId(), expectedPaymentPrice);

        assertThat(pointJpaRepository.findByUserId(userId).get().getPoint()).isEqualTo(expectedUserPoint);
        assertThat(orderJpaRepository.findById(order.getId()).get().getStatus()).isEqualTo(OrderStatus.PAID);

        final IssuedCoupon afterIssuedCoupon = issuedCouponJpaRepository.findById(issuedCoupon.getId()).get();
        assertThat(afterIssuedCoupon.getOrderId()).isEqualTo(order.getId());
        assertThat(afterIssuedCoupon.getUsedAt()).isNotNull();
    }

    @DisplayName("정률할인 쿠폰으로 결제하면 쿠폰에 명시된 할인비율만큼 차감된 금액으로 결제한다.")
    @Test
    void paymentWithRateCoupon() throws Exception {
        // given
        final User user = userJpaRepository.save(User.builder().name("사용자").build());

        final Long userId = user.getId();
        final int userPoint = 10000;
        pointJpaRepository.save(Point.builder().userId(userId).point(userPoint).build());

        final int orderAmount = 10000;
        final Order order = orderJpaRepository.save(
                Order.builder()
                        .userId(userId)
                        .amount(orderAmount)
                        .status(OrderStatus.ORDERED)
                        .build()
        );

        final long couponId = 1L;
        final DiscountType discountType = DiscountType.RATE;
        final int discountValue = 15;
        final IssuedCoupon issuedCoupon = issuedCouponJpaRepository.save(
                IssuedCoupon.builder()
                        .userId(userId)
                        .couponId(couponId)
                        .discountType(discountType)
                        .discountValue(discountValue)
                        .expiredAt(LocalDateTime.now().plusDays(10))
                        .build()
        );

        final int expectedDiscountAmount = (orderAmount * discountValue / 100);
        final int expectedPaymentAmount = orderAmount - expectedDiscountAmount;
        final int expectedUserPoint = userPoint - expectedPaymentAmount;

        final PaymentRequest request = PaymentRequest.builder()
                .orderId(order.getId())
                .userId(userId)
                .couponId(couponId)
                .build();

        // when
        final PaymentResponse result = paymentApplicationService.pay(request);

        // then
        assertThat(result).isNotNull()
                .extracting("orderId", "paymentPrice")
                .containsExactly(order.getId(), expectedPaymentAmount);

        assertThat(pointJpaRepository.findByUserId(userId).get().getPoint()).isEqualTo(expectedUserPoint);
        assertThat(orderJpaRepository.findById(order.getId()).get().getStatus()).isEqualTo(OrderStatus.PAID);

        final IssuedCoupon afterIssuedCoupon = issuedCouponJpaRepository.findById(issuedCoupon.getId()).get();
        assertThat(afterIssuedCoupon.getOrderId()).isEqualTo(order.getId());
        assertThat(afterIssuedCoupon.getUsedAt()).isNotNull();
    }

    @DisplayName("쿠폰을 적용하지 않으면 주문 총 금액을 결제한다.")
    @Test
    void paymentWithNoCoupon() throws Exception {
        // given
        final User user = userJpaRepository.save(User.builder().name("사용자").build());
        final Long userId = user.getId();

        final int userPoint = 10000;
        final int orderAmount = 1000;

        final int expectedPaymentPrice = orderAmount;
        final int expectedUserPoint = userPoint - expectedPaymentPrice;

        pointJpaRepository.save(Point.builder().userId(userId).point(userPoint).build());

        final Order order = orderJpaRepository.save(
                Order.builder()
                        .userId(userId)
                        .amount(orderAmount)
                        .status(OrderStatus.ORDERED)
                        .build()
        );

        final PaymentRequest request = PaymentRequest.builder()
                .orderId(order.getId())
                .userId(userId)
                .couponId(null)
                .build();

        // when
        final PaymentResponse result = paymentApplicationService.pay(request);

        // then
        assertThat(result).isNotNull()
                .extracting("orderId", "paymentPrice")
                .containsExactly(order.getId(), expectedPaymentPrice);

        assertThat(pointJpaRepository.findByUserId(userId).get().getPoint()).isEqualTo(expectedUserPoint);
        assertThat(orderJpaRepository.findById(order.getId()).get().getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @DisplayName("보유 포인트가 최종 결제 금액보다 적으면 IllegalStateException 예외가 발생한다.")
    @Test
    void paymentOverUserPoint() throws Exception {
        // given
        final User user = userJpaRepository.save(User.builder().name("사용자").build());
        final Long userId = user.getId();

        final int userPoint = 1000;
        final int orderAmount = userPoint + 1;
        final int discountValue = 0;

        pointJpaRepository.save(Point.builder().userId(userId).point(userPoint).build());

        final Order order = orderJpaRepository.save(
                Order.builder()
                        .userId(userId)
                        .amount(orderAmount)
                        .status(OrderStatus.ORDERED)
                        .build()
        );

        final long couponId = 1L;
        issuedCouponJpaRepository.save(
                IssuedCoupon.builder()
                        .userId(userId)
                        .couponId(couponId)
                        .discountType(DiscountType.FIXED)
                        .discountValue(discountValue)
                        .expiredAt(LocalDateTime.now().plusDays(10))
                        .build()
        );

        final PaymentRequest request = PaymentRequest.builder()
                .orderId(order.getId())
                .userId(userId)
                .couponId(couponId)
                .build();

        // when &&
        assertThatThrownBy(() -> paymentApplicationService.pay(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("포인트가 부족합니다.");
    }

    @DisplayName("이미 결제한 주문에 대해 결제를 시도하면 IllegalStateException 예외가 발생한다.")
    @Test
    void paymentAlreadyPaid() throws Exception {
        // given
        final User user = userJpaRepository.save(User.builder().name("사용자").build());
        final Long userId = user.getId();

        final int userPoint = 1000;
        final int orderAmount = userPoint + 1;
        final int discountValue = 0;

        pointJpaRepository.save(Point.builder().userId(userId).point(userPoint).build());

        final OrderStatus paid = OrderStatus.PAID;
        final Order order = orderJpaRepository.save(
                Order.builder()
                        .userId(userId)
                        .amount(orderAmount)
                        .status(paid)
                        .build()
        );

        final long couponId = 1L;
        issuedCouponJpaRepository.save(
                IssuedCoupon.builder()
                        .userId(userId)
                        .couponId(couponId)
                        .discountType(DiscountType.FIXED)
                        .discountValue(discountValue)
                        .expiredAt(LocalDateTime.now().plusDays(10))
                        .build()
        );

        final PaymentRequest request = PaymentRequest.builder()
                .orderId(order.getId())
                .userId(userId)
                .couponId(couponId)
                .build();

        // when &&
        assertThatThrownBy(() -> paymentApplicationService.pay(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 결제된 주문입니다.");
    }

    @DisplayName("만료된 쿠폰으로 결제를 시도하면 IllegalStateException 예외가 발생한다.")
    @Test
    void paymentExpiredCoupon() throws Exception {
        // given
        final User user = userJpaRepository.save(User.builder().name("사용자").build());
        final Long userId = user.getId();

        final int userPoint = 1000;
        final int orderAmount = userPoint + 1;
        final int discountValue = 0;

        pointJpaRepository.save(Point.builder().userId(userId).point(userPoint).build());

        final Order order = orderJpaRepository.save(
                Order.builder()
                        .userId(userId)
                        .amount(orderAmount)
                        .status(OrderStatus.ORDERED)
                        .build()
        );

        final long couponId = 1L;
        issuedCouponJpaRepository.save(
                IssuedCoupon.builder()
                        .userId(userId)
                        .couponId(couponId)
                        .discountType(DiscountType.FIXED)
                        .discountValue(discountValue)
                        .expiredAt(LocalDateTime.now().minusMinutes(1))
                        .build()
        );

        final PaymentRequest request = PaymentRequest.builder()
                .orderId(order.getId())
                .userId(userId)
                .couponId(couponId)
                .build();

        // when &&
        assertThatThrownBy(() -> paymentApplicationService.pay(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("만료된 쿠폰입니다.");
    }

    @DisplayName("이미 사용한 쿠폰으로 결제를 시도하면 IllegalStateException 예외가 발생한다.")
    @Test
    void paymentAlreadyUseCoupon() throws Exception {
        // given
        final User user = userJpaRepository.save(User.builder().name("사용자").build());
        final Long userId = user.getId();

        final int userPoint = 1000;
        final int orderAmount = userPoint + 1;
        final int discountValue = 0;

        pointJpaRepository.save(Point.builder().userId(userId).point(userPoint).build());

        final Order order = orderJpaRepository.save(
                Order.builder()
                        .userId(userId)
                        .amount(orderAmount)
                        .status(OrderStatus.ORDERED)
                        .build()
        );

        final long couponId = 1L;
        issuedCouponJpaRepository.save(
                IssuedCoupon.builder()
                        .userId(userId)
                        .couponId(couponId)
                        .discountType(DiscountType.FIXED)
                        .discountValue(discountValue)
                        .expiredAt(LocalDateTime.now().plusDays(10))
                        .usedAt(LocalDateTime.now().minusMinutes(1))
                        .build()
        );

        final PaymentRequest request = PaymentRequest.builder()
                .orderId(order.getId())
                .userId(userId)
                .couponId(couponId)
                .build();

        // when &&
        assertThatThrownBy(() -> paymentApplicationService.pay(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 사용된 쿠폰입니다.");
    }
}
