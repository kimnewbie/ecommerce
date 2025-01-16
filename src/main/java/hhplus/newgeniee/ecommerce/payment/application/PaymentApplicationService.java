package hhplus.newgeniee.ecommerce.payment.application;

import hhplus.newgeniee.ecommerce.coupon.domain.IssuedCoupon;
import hhplus.newgeniee.ecommerce.coupon.domain.IssuedCouponRepository;
import hhplus.newgeniee.ecommerce.order.domain.Order;
import hhplus.newgeniee.ecommerce.order.domain.OrderRepository;
import hhplus.newgeniee.ecommerce.payment.api.request.PaymentRequest;
import hhplus.newgeniee.ecommerce.payment.api.response.PaymentResponse;
import hhplus.newgeniee.ecommerce.payment.domain.DataPlatformClient;
import hhplus.newgeniee.ecommerce.payment.domain.OrderData;
import hhplus.newgeniee.ecommerce.payment.domain.Payment;
import hhplus.newgeniee.ecommerce.payment.domain.PaymentRepository;
import hhplus.newgeniee.ecommerce.point.domain.Point;
import hhplus.newgeniee.ecommerce.point.domain.PointRepository;
import hhplus.newgeniee.ecommerce.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PaymentApplicationService {

    private final PaymentService paymentService;

    private final PaymentRepository paymentRepository;

    private final UserRepository userRepository;
    private final PointRepository pointRepository;

    private final OrderRepository orderRepository;

    private final IssuedCouponRepository issuedCouponRepository;

    private final DataPlatformClient dataPlatformClient;

    @Transactional
    public PaymentResponse pay(final PaymentRequest request) {
        if (!userRepository.existsById(request.getUserId())) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        final Order order = orderRepository.findByIdForUpdate(request.getOrderId())
                .orElseThrow(() -> new IllegalStateException("주문을 찾을 수 없습니다."));

        order.validate();

        // 비관적락
        final IssuedCoupon issuedCoupon = request.getCouponId() == null
                ? IssuedCoupon.emptyCoupon()
                : issuedCouponRepository.findByCouponIdAndUserIdForUpdate(request.getCouponId(), request.getUserId())
                .orElseThrow(() -> new IllegalStateException("쿠폰을 찾을 수 없습니다."));

        // 비관적락
        final Point point = pointRepository.findByUserIdForUpdate(request.getUserId())
                .orElseThrow(() -> new IllegalStateException("포인트 정보를 찾을 수 없습니다."));

        final Payment payment = paymentService.pay(order, point, issuedCoupon);
        paymentRepository.save(payment);

        dataPlatformClient.sendOrderData(OrderData.from(order));

        return PaymentResponse.from(payment);
    }
}
