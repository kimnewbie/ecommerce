package hhplus.newgeniee.ecommerce.payment.application;

import hhplus.newgeniee.ecommerce.coupon.domain.DiscountType;
import hhplus.newgeniee.ecommerce.coupon.domain.IssuedCoupon;
import hhplus.newgeniee.ecommerce.coupon.domain.IssuedCouponRepository;
import hhplus.newgeniee.ecommerce.order.domain.Order;
import hhplus.newgeniee.ecommerce.order.domain.OrderRepository;
import hhplus.newgeniee.ecommerce.order.domain.OrderStatus;
import hhplus.newgeniee.ecommerce.payment.api.request.PaymentRequest;
import hhplus.newgeniee.ecommerce.payment.api.response.PaymentResponse;
import hhplus.newgeniee.ecommerce.payment.domain.DataPlatformClient;
import hhplus.newgeniee.ecommerce.payment.domain.OrderData;
import hhplus.newgeniee.ecommerce.payment.domain.Payment;
import hhplus.newgeniee.ecommerce.payment.domain.PaymentRepository;
import hhplus.newgeniee.ecommerce.point.domain.Point;
import hhplus.newgeniee.ecommerce.point.domain.PointRepository;
import hhplus.newgeniee.ecommerce.user.domain.UserRepository;
import hhplus.newgeniee.ecommerce.util.EntityIdSetter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class PaymentApplicationServiceUnitTest {

    @InjectMocks
    private PaymentApplicationService paymentApplicationService;

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PointRepository pointRepository;
    @Mock
    private IssuedCouponRepository issuedCouponRepository;
    @Mock
    private PaymentService paymentService;
    @Mock
    private DataPlatformClient dataPlatformClient;

    @DisplayName("정액할인 쿠폰으로 결제하면 쿠폰에 명시된 할인 양만큼 차감된 금액으로 결제한다.")
    @Test
    void paymentWithFixedCoupon() throws Exception {
        // given
        final Long userId = 1L;
        given(userRepository.existsById(userId))
                .willReturn(true);

        final Long pointId = 1L;
        final int userPoint = 10000;
        final Point point = Point.builder()
                .userId(userId)
                .point(userPoint)
                .build();

        EntityIdSetter.setId(point, pointId);
        given(pointRepository.findByUserIdForUpdate(userId))
                .willReturn(Optional.of(point));

        final int orderAmount = 1000;
        final Long orderId = 1L;
        final Order order = Order.builder()
                .userId(userId)
                .amount(orderAmount)
                .status(OrderStatus.ORDERED)
                .build();

        EntityIdSetter.setId(order, orderId);
        given(orderRepository.findByIdForUpdate(order.getId()))
                .willReturn(Optional.of(order));

        final Long couponId = 1L;
        final DiscountType discountType = DiscountType.FIXED;
        final int discountValue = 1000;
        final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
                .userId(userId)
                .couponId(couponId)
                .discountType(discountType)
                .discountValue(discountValue)
                .expiredAt(LocalDateTime.now().plusDays(10))
                .build();

        given(issuedCouponRepository.findByCouponIdAndUserIdForUpdate(couponId, userId))
                .willReturn(Optional.of(issuedCoupon));

        final int expectedDiscountAmount = discountValue;
        final int expectedPaymentAmount = orderAmount - expectedDiscountAmount;

        final PaymentRequest request = PaymentRequest.builder()
                .orderId(orderId)
                .userId(userId)
                .couponId(couponId)
                .build();

        final Long paymentId = 1L;
        final Payment payment = Payment.builder()
                .orderId(orderId)
                .amount(expectedPaymentAmount)
                .build();

        EntityIdSetter.setId(payment, paymentId);
        given(paymentService.pay(order, point, issuedCoupon))
                .willReturn(payment);

        // when
        final PaymentResponse result = paymentApplicationService.pay(request);

        // then
        assertThat(result).isNotNull()
                .extracting("orderId", "paymentPrice")
                .containsExactly(orderId, expectedPaymentAmount);

        then(paymentRepository).should(times(1)).save(payment);
        then(dataPlatformClient).should(times(1)).sendOrderData(any(OrderData.class));
    }

    @DisplayName("정률할인 쿠폰으로 결제하면 쿠폰에 명시된 할인비율만큼 차감된 금액으로 결제한다.")
    @Test
    void paymentWithRateCoupon() throws Exception {
        // given
        final Long userId = 1L;
        given(userRepository.existsById(userId))
                .willReturn(true);

        final Long pointId = 1L;
        final int userPoint = 10000;
        final Point point = Point.builder()
                .userId(userId)
                .point(userPoint)
                .build();

        EntityIdSetter.setId(point, pointId);
        given(pointRepository.findByUserIdForUpdate(userId))
                .willReturn(Optional.of(point));

        final int orderAmount = 10000;
        final Long orderId = 1L;
        final Order order = Order.builder()
                .userId(userId)
                .amount(orderAmount)
                .status(OrderStatus.ORDERED)
                .build();

        EntityIdSetter.setId(order, orderId);
        given(orderRepository.findByIdForUpdate(order.getId()))
                .willReturn(Optional.of(order));

        final Long couponId = 1L;
        final DiscountType discountType = DiscountType.RATE;
        final int discountValue = 15;
        final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
                .userId(userId)
                .couponId(couponId)
                .discountType(discountType)
                .discountValue(discountValue)
                .expiredAt(LocalDateTime.now().plusDays(10))
                .build();

        given(issuedCouponRepository.findByCouponIdAndUserIdForUpdate(couponId, userId))
                .willReturn(Optional.of(issuedCoupon));

        final int expectedDiscountAmount = (orderAmount * discountValue / 100);
        final int expectedPaymentAmount = orderAmount - expectedDiscountAmount;

        final PaymentRequest request = PaymentRequest.builder()
                .orderId(orderId)
                .userId(userId)
                .couponId(couponId)
                .build();

        final Long paymentId = 1L;
        final Payment payment = Payment.builder()
                .orderId(orderId)
                .amount(expectedPaymentAmount)
                .build();

        EntityIdSetter.setId(payment, paymentId);
        given(paymentService.pay(order, point, issuedCoupon))
                .willReturn(payment);

        // when
        final PaymentResponse result = paymentApplicationService.pay(request);

        // then
        assertThat(result).isNotNull()
                .extracting("orderId", "paymentPrice")
                .containsExactly(orderId, expectedPaymentAmount);

        then(paymentRepository).should(times(1)).save(payment);
        then(dataPlatformClient).should(times(1)).sendOrderData(any(OrderData.class));
    }

    @DisplayName("쿠폰을 적용하지 않으면 주문 총 금액을 결제한다.")
    @Test
    void paymentWithNoCoupon() throws Exception {
        // given
        final Long userId = 1L;
        given(userRepository.existsById(userId))
                .willReturn(true);

        final Long pointId = 1L;
        final int userPoint = 10000;
        final Point point = Point.builder()
                .userId(userId)
                .point(userPoint)
                .build();

        EntityIdSetter.setId(point, pointId);
        given(pointRepository.findByUserIdForUpdate(userId))
                .willReturn(Optional.of(point));

        final int orderAmount = 10000;
        final Long orderId = 1L;
        final Order order = Order.builder()
                .userId(userId)
                .amount(orderAmount)
                .status(OrderStatus.ORDERED)
                .build();

        EntityIdSetter.setId(order, orderId);
        given(orderRepository.findByIdForUpdate(order.getId()))
                .willReturn(Optional.of(order));


        final int expectedDiscountAmount = 0;
        final int expectedPaymentAmount = orderAmount - expectedDiscountAmount;

        final Long couponId = null;
        final PaymentRequest request = PaymentRequest.builder()
                .orderId(orderId)
                .userId(userId)
                .couponId(couponId)
                .build();

        final Long paymentId = 1L;
        final Payment payment = Payment.builder()
                .orderId(orderId)
                .amount(expectedPaymentAmount)
                .build();

        EntityIdSetter.setId(payment, paymentId);
        given(paymentService.pay(eq(order), eq(point), any(IssuedCoupon.class)))
                .willReturn(payment);

        // when
        final PaymentResponse result = paymentApplicationService.pay(request);

        // then
        assertThat(result).isNotNull()
                .extracting("orderId", "paymentPrice")
                .containsExactly(orderId, expectedPaymentAmount);

        then(paymentRepository).should(times(1)).save(payment);
        then(dataPlatformClient).should(times(1)).sendOrderData(any(OrderData.class));
    }

    @DisplayName("보유 포인트가 최종 결제 금액보다 적으면 IllegalStateException 예외가 발생한다.")
    @Test
    void paymentOverUserPoint() throws Exception {
        // given
        final Long userId = 1L;
        given(userRepository.existsById(userId))
                .willReturn(true);

        final int userPoint = 10000;
        final int orderAmount = userPoint + 1;
        final int discountValue = 0;

        final Long pointId = 1L;
        final Point point = Point.builder()
                .userId(userId)
                .point(userPoint)
                .build();

        EntityIdSetter.setId(point, pointId);
        given(pointRepository.findByUserIdForUpdate(userId))
                .willReturn(Optional.of(point));

        final Long orderId = 1L;
        final Order order = Order.builder()
                .userId(userId)
                .amount(orderAmount)
                .status(OrderStatus.ORDERED)
                .build();

        EntityIdSetter.setId(order, orderId);
        given(orderRepository.findByIdForUpdate(order.getId()))
                .willReturn(Optional.of(order));

        final Long couponId = 1L;
        final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
                .userId(userId)
                .couponId(couponId)
                .discountType(DiscountType.FIXED)
                .discountValue(discountValue)
                .expiredAt(LocalDateTime.now().plusDays(10))
                .build();

        given(issuedCouponRepository.findByCouponIdAndUserIdForUpdate(couponId, userId))
                .willReturn(Optional.of(issuedCoupon));

        final PaymentRequest request = PaymentRequest.builder()
                .orderId(orderId)
                .userId(userId)
                .couponId(couponId)
                .build();

        willThrow(new IllegalStateException("포인트가 부족합니다."))
                .given(paymentService)
                .pay(order, point, issuedCoupon);

        // when &&
        assertThatThrownBy(() -> paymentApplicationService.pay(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("포인트가 부족합니다.");
    }

    @DisplayName("이미 결제한 주문에 대해 결제를 시도하면 IllegalStateException 예외가 발생한다.")
    @Test
    void paymentAlreadyPaid() throws Exception {
        // given
        final Long userId = 1L;
        given(userRepository.existsById(userId))
                .willReturn(true);

        final Long orderId = 1L;
        final Order order = Order.builder()
                .userId(userId)
                .amount(1000)
                .status(OrderStatus.PAID)
                .build();

        EntityIdSetter.setId(order, orderId);
        given(orderRepository.findByIdForUpdate(order.getId()))
                .willReturn(Optional.of(order));

        final PaymentRequest request = PaymentRequest.builder()
                .orderId(orderId)
                .userId(userId)
                .couponId(1L)
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
        final Long userId = 1L;
        given(userRepository.existsById(userId))
                .willReturn(true);

        final int userPoint = 10000;
        final int orderAmount = 1000;
        final int discountValue = 1000;

        final Long pointId = 1L;
        final Point point = Point.builder()
                .userId(userId)
                .point(userPoint)
                .build();
        EntityIdSetter.setId(point, pointId);
        given(pointRepository.findByUserIdForUpdate(userId))
                .willReturn(Optional.of(point));

        final Long orderId = 1L;
        final Order order = Order.builder()
                .userId(userId)
                .amount(orderAmount)
                .status(OrderStatus.ORDERED)
                .build();
        EntityIdSetter.setId(order, orderId);
        given(orderRepository.findByIdForUpdate(order.getId()))
                .willReturn(Optional.of(order));

        final Long couponId = 1L;
        final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
                .userId(userId)
                .couponId(couponId)
                .discountType(DiscountType.FIXED)
                .discountValue(discountValue)
                .expiredAt(LocalDateTime.now().minusMinutes(1))
                .build();

        given(issuedCouponRepository.findByCouponIdAndUserIdForUpdate(couponId, userId))
                .willReturn(Optional.of(issuedCoupon));

        final PaymentRequest request = PaymentRequest.builder()
                .orderId(orderId)
                .userId(userId)
                .couponId(couponId)
                .build();

        willThrow(new IllegalStateException("만료된 쿠폰입니다."))
                .given(paymentService)
                .pay(order, point, issuedCoupon);

        // when &&
        assertThatThrownBy(() -> paymentApplicationService.pay(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("만료된 쿠폰입니다.");
    }

    @DisplayName("이미 사용한 쿠폰으로 결제를 시도하면 IllegalStateException 예외가 발생한다.")
    @Test
    void paymentAlreadyUseCoupon() throws Exception {
        // given
        final Long userId = 1L;
        given(userRepository.existsById(userId))
                .willReturn(true);

        final int userPoint = 10000;
        final int orderAmount = 1000;
        final int discountValue = 1000;

        final Long pointId = 1L;
        final Point point = Point.builder()
                .userId(userId)
                .point(userPoint)
                .build();
        EntityIdSetter.setId(point, pointId);
        given(pointRepository.findByUserIdForUpdate(userId))
                .willReturn(Optional.of(point));

        final Long orderId = 1L;
        final Order order = Order.builder()
                .userId(userId)
                .amount(orderAmount)
                .status(OrderStatus.ORDERED)
                .build();
        EntityIdSetter.setId(order, orderId);
        given(orderRepository.findByIdForUpdate(order.getId()))
                .willReturn(Optional.of(order));

        final Long couponId = 1L;
        final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
                .userId(userId)
                .couponId(couponId)
                .discountType(DiscountType.FIXED)
                .discountValue(discountValue)
                .expiredAt(LocalDateTime.now().plusDays(10))
                .usedAt(LocalDateTime.now().minusDays(1))
                .build();

        given(issuedCouponRepository.findByCouponIdAndUserIdForUpdate(couponId, userId))
                .willReturn(Optional.of(issuedCoupon));

        final PaymentRequest request = PaymentRequest.builder()
                .orderId(orderId)
                .userId(userId)
                .couponId(couponId)
                .build();

        willThrow(new IllegalStateException("이미 사용된 쿠폰입니다."))
                .given(paymentService)
                .pay(order, point, issuedCoupon);

        // when &&
        assertThatThrownBy(() -> paymentApplicationService.pay(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 사용된 쿠폰입니다.");
    }
}
