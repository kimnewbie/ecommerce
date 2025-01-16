package hhplus.newgeniee.ecommerce.point.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "point")
@Entity
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int point;

    @Builder
    private Point(final Long userId, final int point) {
        if (point < 0) {
            throw new IllegalArgumentException("포인트는 0 이상이어야 합니다.");
        }
        this.userId = userId;
        this.point = point;
    }

    public static Point empty(final long userId) {
        return Point.builder()
                .userId(userId)
                .point(0)
                .build();
    }

    public void reload(final int reloadPoint) {
        if (reloadPoint <= 0) {
            throw new IllegalArgumentException("충전 포인트는 0 이상이어야 합니다.");
        }
        this.point += reloadPoint;
    }

    public void use(final int amount) {
        if (this.point < amount) {
            throw new IllegalStateException("포인트가 부족합니다.");
        }
        this.point -= amount;
    }
}
