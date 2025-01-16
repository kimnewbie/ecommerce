package hhplus.newgeniee.ecommerce.payment.infrastructure;

import hhplus.newgeniee.ecommerce.payment.domain.Payment;
import hhplus.newgeniee.ecommerce.payment.domain.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(final Payment payment) {
        return paymentJpaRepository.save(payment);
    }
}
