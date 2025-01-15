package hhplus.newgeniee.ecommerce.point.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * [1] 포인트 객체 생성
 * [2] 포인트 충전
 */
class PointTest {

    @DisplayName("포인트 객체 생성")
    @Nested
    class createPointNest {

        @DisplayName("사용자 아이디와 보유 포인트로 포인트 객체를 생성할 수 있다.")
        @Test
        void createPoint() throws Exception {
            // given
            final Long userId = 1L;
            final int point = 1000;

            // when
            final Point result = Point.builder()
                    .userId(userId)
                    .point(point)
                    .build();

            // then
            assertThat(result).isNotNull()
                    .extracting("userId", "point")
                    .contains(userId, point);
        }

        @DisplayName("보유 포인트가 음수인 포인트 객체를 생성하려고 하면 IllegalArgumentException 예외가 발생한다.")
        @Test
        void createNotPositivePointThrowsException() {
            // given
            final Long userId = 1L;
            final int point = -1;

            // when & then
            assertThatThrownBy(() -> Point.builder()
                    .userId(userId)
                    .point(point)
                    .build())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("포인트는 0 이상이어야 합니다.");
        }

        @DisplayName("유저 아이디로 보유 포인트가 0인 포인트 객체를 생성할 수 있다.")
        @Test
        void createEmptyPoint() {
            // given
            final Long userId = 1L;

            // when
            final Point result = Point.empty(userId);

            // then
            assertThat(result).isNotNull()
                    .extracting("userId", "point")
                    .containsExactly(userId, 0);
        }
    }

    @DisplayName("포인트 충전")
    @Nested
    class chargePointNest {

        @DisplayName("포인트 충전에 성공하면 보유 포인트와 충전 요청 포인트가 합산된 포인트가 된다.")
        @Test
        void createEmptyPoint() {
            // given
            final Long userId = 1L;
            final int beforePoint = 1000;
            final int amount = 1000;

            final Point point = Point.builder()
                    .userId(userId)
                    .point(beforePoint)
                    .build();

            // when
            point.reload(amount);

            // then
            assertThat(point).isNotNull()
                    .extracting("userId", "point")
                    .containsExactly(userId, 2000);
        }

        @DisplayName("포인트 충전시 충전 요청 포인트가 0이면 IllegalArgumentException  예외가 발생한다.")
        @Test
        void chargeWithZeroAmount() {
            // given
            final int chargePoint = 0;
            final Point point = Point.empty(1L);

            // when & then
            assertThatThrownBy(() -> point.reload(chargePoint))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("충전 포인트는 0 이상이어야 합니다.");
        }

        @DisplayName("포인트 충전시 충전 요청 포인트가 음수이면 IllegalArgumentException 예외가 발생한다.")
        @Test
        void chargeWithNegativeAmount() {
            // given
            final int chargePoint = -10;
            final Point point = Point.empty(1L);

            // when & then
            assertThatThrownBy(() -> point.reload(chargePoint))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("충전 포인트는 0 이상이어야 합니다.");
        }
    }
}