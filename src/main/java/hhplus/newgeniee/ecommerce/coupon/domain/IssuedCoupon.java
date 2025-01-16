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
@AttributeOverride(name = "createdAt", column = @Column(name = "issued_at")) // createdAt (Parents)
@Table(name = "issued_coupon")
@Entity
public class IssuedCoupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long couponId;

    @Column
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private DiscountType discountType;

    @Column(nullable = false)
    private int discountValue;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Column
    private LocalDateTime usedAt;

    @Builder
    private IssuedCoupon(
            final Long userId,
            final Long couponId,
            final Long orderId,
            final DiscountType discountType,
            final int discountValue,
            final LocalDateTime expiredAt,
            final LocalDateTime usedAt
    ) {
        if (discountType == DiscountType.RATE && discountValue > 100) {
            throw new IllegalArgumentException("할인율은 100을 초과할 수 없습니다.");
        }

        this.userId = userId;
        this.couponId = couponId;
        this.orderId = orderId;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.expiredAt = expiredAt;
        this.usedAt = usedAt;
    }

    public LocalDateTime getIssuedAt() {
        return this.getCreatedAt();
    }

    public static IssuedCoupon emptyCoupon() {
        return IssuedCoupon.builder()
                .discountType(DiscountType.NONE)
                .discountValue(0)
                .expiredAt(LocalDateTime.MAX)
                .usedAt(null)
                .build();
    }

    private void validate(final LocalDateTime dateTime) {
        if (this.expiredAt.isBefore(dateTime)) {
            throw new IllegalStateException("만료된 쿠폰입니다.");
        }

        if (this.usedAt != null) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }
    }

    public void use(final Long orderId, final LocalDateTime usedAt) {
        validate(usedAt);
        this.orderId = orderId;
        this.usedAt = usedAt;
    }
}
