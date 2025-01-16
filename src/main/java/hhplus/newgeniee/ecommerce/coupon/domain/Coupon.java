package hhplus.newgeniee.ecommerce.coupon.domain;

import hhplus.newgeniee.ecommerce.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is null")
@Table(name = "coupon")
@Entity
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(nullable = false)
    private int issueLimit;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private DiscountType discountType;

    @Column(nullable = false)
    private int discountValue;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Builder
    private Coupon(
            final String name,
            final int issueLimit,
            final int quantity,
            final DiscountType discountType,
            final int discountValue,
            final LocalDateTime expiredAt
    ) {
        if (discountType == DiscountType.RATE && discountValue > 100) {
            throw new IllegalArgumentException("할인율은 100을 초과할 수 없습니다.");
        }

        this.name = name;
        this.issueLimit = issueLimit;
        this.quantity = quantity;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.expiredAt = expiredAt;
    }

    public void updateQuantity(final int quantity) {
        this.quantity = quantity;
    }
}
