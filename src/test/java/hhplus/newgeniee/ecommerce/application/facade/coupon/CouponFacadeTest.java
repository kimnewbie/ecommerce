package hhplus.newgeniee.ecommerce.application.facade.coupon;

import hhplus.newgeniee.ecommerce.api.dto.response.CommonResponse;
import hhplus.newgeniee.ecommerce.domain.coupon.Coupon;
import hhplus.newgeniee.ecommerce.domain.coupon.history.CouponHistory;
import hhplus.newgeniee.ecommerce.domain.coupon.CouponRepository;
import hhplus.newgeniee.ecommerce.domain.coupon.history.CouponHistoryRepository;
import hhplus.newgeniee.ecommerce.domain.user.User;
import hhplus.newgeniee.ecommerce.domain.user.UserRepository;
import hhplus.newgeniee.ecommerce.domain.user.point.PointTransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.*;

import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

class CouponFacadeTest {

    @Mock
    private CouponRepository couponRepo;

    @Mock
    private CouponHistoryRepository couponHistoryRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private CouponFacade couponFacade;

    private User user;
    private Coupon coupon;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 테스트를 위한 사용자 및 쿠폰 객체 초기화
        user = new User(1L, "testuser", PointTransactionType.INIT, BigDecimal.ZERO, LocalDateTime.now());
        coupon = new Coupon(1L, new BigDecimal("0.10"), "ISSUED", LocalDateTime.now(), LocalDateTime.now().plusDays(7), 0, 30);

        // mock 객체 설정
        when(userRepo.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(couponRepo.findById(coupon.getCouponId())).thenReturn(Optional.of(coupon));
    }

    @Test
    @DisplayName("쿠폰을 성공적으로 발급하고 히스토리 저장")
    void issueCouponAndSaveHistory() {
        // given
        CouponHistory history = new CouponHistory(1L, user, coupon, LocalDateTime.now());
        when(couponHistoryRepo.findByUserAndCoupon(user, coupon)).thenReturn(Optional.empty()); // 쿠폰 미발급 상태
        when(couponHistoryRepo.save(any(CouponHistory.class))).thenReturn(history);

        // 쿠폰 발급 가능 상태로 설정
        coupon.builder().status("ISSUED").build();
        when(couponRepo.findById(coupon.getCouponId())).thenReturn(Optional.of(coupon));

        // when
        CommonResponse response = couponFacade.issueCouponAndSaveHistory(coupon.getCouponId(), user.getUserId());

        // then
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("쿠폰이 성공적으로 발급되었습니다.", response.getMessage());
        verify(couponRepo).save(coupon); // 쿠폰 저장 확인
        verify(couponHistoryRepo).save(history); // 쿠폰 히스토리 저장 확인
    }

    @Test
    @DisplayName("사용자가 이미 쿠폰을 발급받은 경우")
    void whenAlreadyGotCoupon() {
        // given
        CouponHistory existingHistory = new CouponHistory(1L, user, coupon, LocalDateTime.now());
        when(couponHistoryRepo.findByUserAndCoupon(user, coupon)).thenReturn(Optional.of(existingHistory));

        // when
        CommonResponse response = couponFacade.issueCouponAndSaveHistory(coupon.getCouponId(), user.getUserId());

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("사용자는 이미 이 쿠폰을 발급받았습니다.", response.getMessage());
        verify(couponRepo, never()).save(coupon); // 쿠폰 발급되지 않음
    }

    @Test
    @DisplayName("쿠폰이 발급 불가한 상태일 경우")
    void cantIssueCoupon() {
        // given
        coupon.builder().status("PENDING").build(); // 쿠폰 상태가 PENDING이면 발급 불가
        when(couponRepo.findById(coupon.getCouponId())).thenReturn(Optional.of(coupon));

        // when
        CommonResponse response = couponFacade.issueCouponAndSaveHistory(coupon.getCouponId(), user.getUserId());

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("쿠폰이 유효하지 않습니다.", response.getMessage());
        verify(couponRepo, never()).save(coupon); // 쿠폰 발급되지 않음
    }

    @Test
    @DisplayName("사용자를 찾을 수 없는 경우")
    void cantFindUser() {
        // given
        when(userRepo.findById(user.getUserId())).thenReturn(Optional.empty()); // 사용자가 존재하지 않음

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            couponFacade.issueCouponAndSaveHistory(coupon.getCouponId(), user.getUserId());
        });

        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("쿠폰을 찾을 수 없는 경우")
    void cantFindCoupon() {
        // given
        when(couponRepo.findById(coupon.getCouponId())).thenReturn(Optional.empty()); // 쿠폰이 존재하지 않음

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            couponFacade.issueCouponAndSaveHistory(coupon.getCouponId(), user.getUserId());
        });

        assertEquals("쿠폰을 찾을 수 없습니다.", exception.getMessage());
    }
}
