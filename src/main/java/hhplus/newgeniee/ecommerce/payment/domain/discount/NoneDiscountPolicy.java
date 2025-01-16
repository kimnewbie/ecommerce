package hhplus.newgeniee.ecommerce.payment.domain.discount;

import org.springframework.stereotype.Component;

@Component
public class NoneDiscountPolicy implements DiscountPolicy {

    @Override
    public int calculateDiscountAmount(final int price, final int discountValue) {
        return 0;
    }
}
