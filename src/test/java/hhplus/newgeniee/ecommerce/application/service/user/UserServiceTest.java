package hhplus.newgeniee.ecommerce.application.service.user;

import hhplus.newgeniee.ecommerce.domain.user.User;
import hhplus.newgeniee.ecommerce.domain.user.UserRepository;
import hhplus.newgeniee.ecommerce.domain.user.point.PointTransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static hhplus.newgeniee.ecommerce.domain.user.point.PointTransactionType.RELOAD;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        // Mockito 초기화
        MockitoAnnotations.openMocks(this);

        // 테스트용 User 객체 생성
        user = User.builder()
                .username("testuser")
                .balance(BigDecimal.ZERO)
                .build();
    }

    @Test
    @DisplayName("주어진 사용자 이름으로 사용자를 생성하면 새로운 사용자가 저장된다")
    void createUser() {
        // given
        String username = "newuser";
        User newUser = User.builder()
                .username(username)
                .balance(BigDecimal.ZERO)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // when
        User createdUser = userService.createUser(username);

        // then
        assertNotNull(createdUser);
        assertEquals(username, createdUser.getUsername());
        assertEquals(BigDecimal.ZERO, createdUser.getBalance());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("사용자 ID와 금액이 주어지면 잔액이 업데이트된다")
    void updateUserPoint() {
        // given
        Long userId = 1L;
        BigDecimal amount = new BigDecimal("100.00");
        User updatedUser = user.toBuilder()
                .balance(user.getBalance().add(amount))
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // when
        User result = userService.updateUserBalance(user, RELOAD, amount);

        // then
        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.getBalance());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("유효하지 않은 사용자 ID가 주어지면 예외가 발생한다")
    void checkUserId() {
        // given
        Long invalidUserId = 999L;
        BigDecimal amount = new BigDecimal("100.00");
        User nonExistentUser = User.builder()
                .userId(invalidUserId)
                .balance(new BigDecimal("200.00"))
                .build();
        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> userService.updateUserBalance(nonExistentUser, PointTransactionType.RELOAD, amount));
        verify(userRepository, times(1)).findById(invalidUserId);
    }
}
