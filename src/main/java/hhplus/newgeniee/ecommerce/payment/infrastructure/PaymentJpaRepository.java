package hhplus.newgeniee.ecommerce.payment.infrastructure;

import hhplus.newgeniee.ecommerce.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
}
