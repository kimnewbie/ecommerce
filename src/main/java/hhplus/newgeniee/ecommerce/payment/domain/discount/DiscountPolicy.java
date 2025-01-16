package hhplus.newgeniee.ecommerce.payment.domain.discount;

public interface DiscountPolicy {

    int calculateDiscountAmount(int price, int discountValue);
}
