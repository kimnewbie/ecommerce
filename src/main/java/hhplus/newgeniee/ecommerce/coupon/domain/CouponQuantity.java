package hhplus.newgeniee.ecommerce.coupon.domain;

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
@Table(name = "coupon_quantity")
@Entity
public class CouponQuantity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long couponId;

    @Column(nullable = false)
    private int quantity;

    @Builder
    private CouponQuantity(final Long couponId, final int quantity) {
        this.couponId = couponId;
        this.quantity = quantity;
    }

    public void issueCoupon() {
        if (quantity <= 0) {
            throw new IllegalArgumentException("쿠폰 수량이 부족합니다.");
        }
        this.quantity--;
    }
}
