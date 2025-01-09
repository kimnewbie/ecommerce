package hhplus.newgeniee.ecommerce.application.service.user.point;

import hhplus.newgeniee.ecommerce.domain.user.User;
import hhplus.newgeniee.ecommerce.domain.user.point.PointTransactionType;
import hhplus.newgeniee.ecommerce.domain.user.point.UserPoint;
import hhplus.newgeniee.ecommerce.domain.user.point.UserPointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static hhplus.newgeniee.ecommerce.domain.user.point.PointTransactionType.RELOAD;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserPointServiceTest {

    @Mock
    private UserPointRepository userPointRepository;

    @InjectMocks
    private UserPointService userPointService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 테스트용 User 객체 생성
        user = User.builder()
                .username("testuser")
                .balance(BigDecimal.ZERO)
                .build();
    }

    @Test
    @DisplayName("주어진 사용자에 대해 포인트를 추가하면 저장된다")
    void reloadUserPoint() {
        // given
        BigDecimal balance = new BigDecimal("1000.00");

        UserPoint userPoint = UserPoint.builder()
                .user(user)
                .type(RELOAD)
                .balance(balance)
                .createdAt(LocalDateTime.now())
                .build();

        when(userPointRepository.save(any(UserPoint.class))).thenReturn(userPoint);

        // when
        UserPoint savedUserPoint = userPointService.addUserPoint(user, RELOAD, balance);

        assertNotNull(savedUserPoint, "저장된 UserPoint가 null입니다!");  // assertNotNull에 메시지를 추가해 이유를 쉽게 알 수 있음
        assertEquals(RELOAD, savedUserPoint.getType());
        assertEquals(balance, savedUserPoint.getBalance());

        // then
        verify(userPointRepository, times(1)).save(any(UserPoint.class));
    }


    @Test
    @DisplayName("주어진 사용자에 대한 포인트 내역을 조회하면 리스트가 반환된다")
    void getUserPointHistory() {
        // given
        UserPoint userPoint1 = UserPoint.builder()
                .user(user)
                .type(PointTransactionType.REWARD)
                .balance(new BigDecimal("1000.00"))
                .createdAt(LocalDateTime.now())
                .build();
        UserPoint userPoint2 = UserPoint.builder()
                .user(user)
                .type(PointTransactionType.USE)
                .balance(new BigDecimal("1200.00"))
                .createdAt(LocalDateTime.now())
                .build();

        when(userPointRepository.findByUser(user)).thenReturn(List.of(userPoint1, userPoint2));

        // when
        List<UserPoint> userPoints = userPointService.getUserPoints(user);

        // then
        assertNotNull(userPoints);
        assertEquals(2, userPoints.size());
        verify(userPointRepository, times(1)).findByUser(user);
    }

    @Test
    @DisplayName("주어진 사용자에 대한 최신 포인트 내역을 조회하면 가장 최근 포인트 내역이 반환된다")
    void getLatestUserPoint() {
        // given
        UserPoint userPoint = UserPoint.builder()
                .user(user)
                .type(PointTransactionType.REWARD)
                .balance(new BigDecimal("1000.00"))
                .createdAt(LocalDateTime.now())
                .build();

        when(userPointRepository.findTopByUserOrderByCreatedAtDesc(user)).thenReturn(Optional.of(userPoint));

        // when
        Optional<UserPoint> latestUserPoint = userPointService.getLatestUserPoint(user);

        // then
        assertTrue(latestUserPoint.isPresent());
        assertEquals(userPoint, latestUserPoint.get());
        verify(userPointRepository, times(1)).findTopByUserOrderByCreatedAtDesc(user);
    }
}
