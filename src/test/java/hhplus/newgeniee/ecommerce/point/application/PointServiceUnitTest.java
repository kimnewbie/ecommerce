package hhplus.newgeniee.ecommerce.point.application;

import hhplus.newgeniee.ecommerce.point.api.request.PointReloadRequest;
import hhplus.newgeniee.ecommerce.point.api.response.PointReloadResponse;
import hhplus.newgeniee.ecommerce.point.api.response.PointResponse;
import hhplus.newgeniee.ecommerce.point.domain.Point;
import hhplus.newgeniee.ecommerce.point.domain.PointRepository;
import hhplus.newgeniee.ecommerce.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

/**
 * [1] 포인트 조회
 * [2] 포인트 충전
 */
@ExtendWith(MockitoExtension.class)
class PointServiceUnitTest {

    /**
     * Mock 객체들이 주입되는 곳
     */
    @InjectMocks
    private PointService pointService;

    @Mock
    private PointRepository pointRepository;

    @Mock
    private UserRepository userRepository;

    @DisplayName("포인트 조회")
    @Nested
    class GetPoint {

        @DisplayName("사용자 포인트를 사용자 아이디를 통해 조회할 수 있다.")
        @Test
        void getPointByUserId() throws Exception {
            // given
            final long userId = 1L;
            final int userPoint = 10000;

            given(userRepository.existsById(userId))
                    .willReturn(true);

            final Point point = Point.builder()
                    .userId(userId)
                    .point(userPoint)
                    .build();

            given(pointRepository.findByUserId(userId))
                    .willReturn(Optional.of(point));

            // when
            final PointResponse result = pointService.getPoint(userId);

            // then
            assertThat(result).isNotNull()
                    .extracting("userId", "point")
                    .contains(userId, userPoint);
        }

        @DisplayName("사용자가 보유한 포인트가 없는 경우 0포인트를 반환한다.")
        @Test
        void getPointByUserIdNoPoint() throws Exception {
            // given
            final long userId = 1L;
            final int noPoint = 0;

            // 사용자가 존재
            given(userRepository.existsById(userId))
                    .willReturn(true);

            // 포인트가 0
            given(pointRepository.findByUserId(userId))
                    .willReturn(Optional.empty());

            final Point point = Point.builder()
                    .userId(userId)
                    .point(noPoint)
                    .build();

            // 어떤 값의 Point 객체라도 저장된다.
            given(pointRepository.save(any(Point.class)))
                    .willReturn(point);

            // when
            final PointResponse result = pointService.getPoint(userId);

            // then
            assertThat(result).isNotNull()
                    .extracting("userId", "point")
                    .containsExactly(userId, noPoint);

            then(pointRepository).should(times(1)).save(any(Point.class));
        }
        
        @DisplayName("존재하지 않는 사용자의 포인트를 조회하면 NoSuchElementException 예외가 발생한다.")
        @Test
        void getPointByInvalidUser() throws Exception {
            // given
            final long userId = 1L;

            given(userRepository.existsById(userId))
                    .willReturn(false);

            // when & then
            assertThatThrownBy(() -> pointService.getPoint(userId))
                    .isInstanceOf(NoSuchElementException.class)
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
            final long userId = 1L;
            given(userRepository.existsById(userId))
                    .willReturn(true);
            final int userPoint = 5000;
            final int reloadPoint = 10000;

            final int expectedPoint = userPoint + reloadPoint;

            final PointReloadRequest request = PointReloadRequest.builder()
                    .userId(userId)
                    .reloadPoint(reloadPoint)
                    .build();

            final Point point = Point.builder()
                    .userId(userId)
                    .point(userPoint)
                    .build();

            given(pointRepository.findByUserIdForUpdate(userId))
                    .willReturn(Optional.of(point));

            // when
            final PointReloadResponse result = pointService.reload(request);

            // then
            assertThat(result.getPoint()).isEqualTo(expectedPoint);
        }
    }


    @DisplayName("유효하지 않은 사용자의 포인트 충전을 요청하면 NoSuchElementException 예외가 발생한다.")
    @Test
    void chargeInvalidUserPoint() throws Exception {
        // given
        final long userId = 1L;
        given(userRepository.existsById(userId))
                .willReturn(false);

        final PointReloadRequest request = PointReloadRequest.builder()
                .userId(userId)
                .build();

        // when
        assertThatThrownBy(() -> pointService.reload(request))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("충전 요청 포인트가 양수가 아니면 IllegalArgumentException 예외가 발생한다.")
    @Test
    void chargeWithNotPositiveChargePoint() throws Exception {
        // given
        final long userId = 1L;
        given(userRepository.existsById(userId))
                .willReturn(true);

        final int pointHeld = 1000;
        final Point point = Point.builder()
                .userId(userId)
                .point(pointHeld)
                .build();

        given(pointRepository.findByUserIdForUpdate(userId))
                .willReturn(Optional.of(point));

        final int chargePoint = 0;
        final PointReloadRequest request = PointReloadRequest.builder()
                .userId(userId)
                .reloadPoint(chargePoint)
                .build();

        // when & then
        assertThatThrownBy(() -> pointService.reload(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("충전 포인트는 0 이상이어야 합니다.");
    }
}