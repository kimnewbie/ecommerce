package hhplus.newgeniee.ecommerce.point.application;

import hhplus.newgeniee.ecommerce.common.ServiceIntegrationTest;
import hhplus.newgeniee.ecommerce.point.api.request.PointReloadRequest;
import hhplus.newgeniee.ecommerce.point.domain.Point;
import hhplus.newgeniee.ecommerce.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 동시성 테스트
 */
public class PointApplicationServiceConcurrencyTest extends ServiceIntegrationTest {

    @DisplayName("한 명의 사용자가 동시에 50번의 충전 요청을 해도 모든 요청이 성공한다.")
    @Test
    void oneUserReload50times() throws Exception {
        // given
        final User user = userJpaRepository.save(User.builder().name("사용자").build());

        final Long userId = user.getId();
        final int point = 0;

        pointJpaRepository.save(Point.builder().userId(userId).point(point).build());

        final int chargePoint = 1000;
        final int chargeTryCount = 50;
        
        final int expectedPoint = point + (chargePoint * chargeTryCount);

        final PointReloadRequest request = PointReloadRequest.builder()
                .userId(userId)
                .reloadPoint(chargePoint)
                .build();

        final List<CompletableFuture<Boolean>> tasks = new ArrayList<>(chargeTryCount);

        // 멀티스레드 환경에서 안전하게 값을 수정할 수 있는 정수
        final AtomicInteger exceptionCount = new AtomicInteger(0);

        // when
        // 비동기 작업 실행
        for (int i = 1; i <= chargeTryCount; i++) {
            tasks.add(CompletableFuture.supplyAsync(() -> {
                pointService.reload(request);
                return true;
            }).exceptionally(e -> {
                exceptionCount.incrementAndGet();
                return false;
            }));
        }

        // then
        // 모든 비동기 작업 완료
        // List<CompletableFuture>를 배열로 변환
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();

        int successCount = 0;
        int failureCount = 0;

        for (CompletableFuture<Boolean> task : tasks) {
            if (task.get()) {
                successCount++;
            } else {
                failureCount++;
            }
        }

        assertThat(exceptionCount.get()).isEqualTo(0);
        assertThat(successCount).isEqualTo(50);
        assertThat(failureCount).isEqualTo(exceptionCount.get());

        assertThat(pointJpaRepository.findByUserId(userId).get().getPoint()).isEqualTo(expectedPoint);
    }
}
