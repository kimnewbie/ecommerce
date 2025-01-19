package hhplus.newgeniee.ecommerce.point.application;

import hhplus.newgeniee.ecommerce.common.ServiceIntegrationTest;
import hhplus.newgeniee.ecommerce.point.api.request.PointReloadRequest;
import hhplus.newgeniee.ecommerce.point.api.response.PointReloadResponse;
import hhplus.newgeniee.ecommerce.point.api.response.PointResponse;
import hhplus.newgeniee.ecommerce.point.domain.Point;
import hhplus.newgeniee.ecommerce.user.domain.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * [1] 포인트 조회
 * [2] 포인트 충전
 */
class PointApplicationServiceIntegrationTest extends ServiceIntegrationTest {

    @DisplayName("포인트 조회")
    @Nested
    class GetPoint {

        @DisplayName("사용자 포인트를 사용자 아이디를 통해 조회할 수 있다.")
        @Test
        void getPointByUserId() throws Exception {
            // given
            final User user = userJpaRepository.save(User.builder().name("사용자2").build());

            final Long userId = user.getId();
            final int userPoint = 10000;

            pointJpaRepository.save(Point.builder()
                    .userId(user.getId())
                            .point(userPoint)
                            .build());

            // when
            final PointResponse result = pointService.getPoint(userId);

            // then
            assertThat(result).isNotNull()
                    .extracting("userId", "point")
                    .containsExactly(userId, userPoint);
        }

        @DisplayName("사용자가 보유한 포인트가 없는 경우 0포인트를 반환한다.")
        @Test
        void getPointByUserIdNoPoint() throws Exception {
            // given
            final User user = userJpaRepository.save(User.builder().name("사용자1").build());

            final Long userId = user.getId();
            final int noPoint = 0;

            // when
            final PointResponse result = pointService.getPoint(userId);

            // then
            assertThat(result).isNotNull()
                    .extracting("userId", "point")
                    .containsExactly(userId, noPoint);
        }

        @DisplayName("존재하지 않는 사용자의 포인트를 조회하면 EntityNotFoundException 예외가 발생한다.")
        @Test
        void getPointByInvalidUser() throws Exception {
            // given
            final long userId = System.currentTimeMillis();

            // when & then
            assertThatThrownBy(() -> pointService.getPoint(userId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("사용자를 찾을 수 없습니다.");
        }
    }

    @DisplayName("포인트 충전")
    @Nested
    class ReloadPoint {
        @DisplayName("포인트를 충전할 수 있다.")
        @Test
        void reloadPoint() throws Exception {
            // given
            final User user = userJpaRepository.save(User.builder().name("사용자1").build());

            final Long userId = user.getId();
            final int userPoint = 5000;

            pointJpaRepository.save(Point.builder().userId(userId).point(userPoint).build());

            final int reloadPoint = 10000;

            final PointReloadRequest request = PointReloadRequest.builder()
                    .userId(userId)
                    .reloadPoint(reloadPoint)
                    .build();


            final int expectedPoint = userPoint + reloadPoint;

            // when
            final PointReloadResponse result = pointService.reload(request);

            // then
            assertThat(result.getPoint()).isEqualTo(expectedPoint);
        }
    }


    @DisplayName("유효하지 않은 사용자의 포인트 충전을 요청하면 EntityNotFoundException 예외가 발생한다.")
    @Test
    void reloadInvalidUserPoint() throws Exception {
        // given
        final Long userId = System.currentTimeMillis();

        final PointReloadRequest request = PointReloadRequest.builder()
                .userId(userId)
                .reloadPoint(1000)
                .build();

        // when
        assertThatThrownBy(() -> pointService.reload(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("충전 요청 포인트가 양수가 아니면 IllegalArgumentException 예외가 발생한다.")
    @Test
    void reloadWithNotPositiveReloadPoint() throws Exception {
        // given
        final User user = userJpaRepository.save(User.builder().name("사용자1").build());

        final Long userId = user.getId();
        final int userPoint = 5000;

        pointJpaRepository.save(Point.builder().userId(userId).point(userPoint).build());

        final int reloadPoint = 0;

        final PointReloadRequest request = PointReloadRequest.builder()
                .userId(userId)
                .reloadPoint(reloadPoint)
                .build();

        // when & then
        assertThatThrownBy(() -> pointService.reload(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("충전 포인트는 0 이상이어야 합니다.");
    }
}
