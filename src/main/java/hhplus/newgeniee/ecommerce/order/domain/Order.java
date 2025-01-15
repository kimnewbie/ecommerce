package hhplus.newgeniee.ecommerce.order.domain;


import hhplus.newgeniee.ecommerce.global.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.SQLRestriction;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is null")
@Table(name = "order")
@Entity
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private OrderStatus status;

    @Builder
    private Order(final Long userId, final int amount, final OrderStatus status) {
        this.userId = userId;
        this.amount = amount;
        this.status = status;
    }

    public static Order createOrder(final Long userId) {
        return Order.builder()
                .userId(userId)
                .amount(0)
                .status(OrderStatus.ORDERED)
                .build();
    }

    public void addOrderItem(final OrderItem item) {
        this.amount += item.calculatePrice();
    }

    public int calculatePaymentPrice(int discountAmount) {
        if (this.amount < discountAmount) {
            throw new IllegalArgumentException("할인 금액이 결제 금액보다 큽니다.");
        }
        return this.amount - discountAmount;
    }

    public void updatePaymentStatus() {
        validate();
        this.status = OrderStatus.PAID;
    }

    public void validate() {
        if (this.status == OrderStatus.PAID) {
            throw new IllegalStateException("이미 결제된 주문입니다.");
        }
    }
}
