package hhplus.newgeniee.ecommerce.payment.application;

import hhplus.newgeniee.ecommerce.coupon.domain.IssuedCoupon;
import hhplus.newgeniee.ecommerce.order.domain.Order;
import hhplus.newgeniee.ecommerce.payment.domain.Payment;
import hhplus.newgeniee.ecommerce.payment.domain.discount.DiscountCalculator;
import hhplus.newgeniee.ecommerce.point.domain.Point;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final DiscountCalculator discountCalculator;

    public Payment pay(final Order order, final Point point, final IssuedCoupon issuedCoupon) {
        final LocalDateTime paymentAt = LocalDateTime.now();
        issuedCoupon.use(order.getId(), paymentAt);

        final int discountAmount = discountCalculator.calculateDiscountAmount(order, issuedCoupon);
        final int paymentAmount = order.calculatePaymentPrice(discountAmount);
        point.use(paymentAmount);

        order.updatePaymentStatus();

        return Payment.builder()
                .orderId(order.getId())
                .amount(paymentAmount)
                .build();
    }
}