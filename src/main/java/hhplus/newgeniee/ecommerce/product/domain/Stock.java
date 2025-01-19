package hhplus.newgeniee.ecommerce.product.domain;

import hhplus.newgeniee.ecommerce.global.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "stock")
@Entity
public class Stock extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    @Builder
    private Stock(final Long productId, final int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public void decrease(final int quantity) {
        if (this.quantity < quantity) {
            throw new IllegalArgumentException("주문 수량이 현재 를 초과할 수 없습니다.");
        }
        this.quantity -= quantity;
    }
}
