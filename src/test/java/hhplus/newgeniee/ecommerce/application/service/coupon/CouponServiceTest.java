package hhplus.newgeniee.ecommerce.application.service.coupon;

import hhplus.newgeniee.ecommerce.application.facade.coupon.CouponFacade;
import hhplus.newgeniee.ecommerce.domain.coupon.Coupon;
import hhplus.newgeniee.ecommerce.domain.coupon.CouponRepository;
import hhplus.newgeniee.ecommerce.domain.coupon.history.CouponHistoryRepository;
import hhplus.newgeniee.ecommerce.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponHistoryRepository couponHistoryRepository;

    @Mock
    private CouponFacade couponFacade;

    @Mock
    private User user;

    @InjectMocks
    private CouponService couponService;

    private Coupon coupon;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        coupon = Coupon.builder()
                .couponId(1L)
                .discountRate(new BigDecimal("0.10"))
                .status("PENDING")
                .issuedAt(null)
                .expirationAt(null)
                .issuedCount(0)
                .maxIssuableCount(30)
                .build();
    }

    @Test
    void testIssueCouponWhenMaxCountReached() {
        coupon.builder().issuedCount(30); // 쿠폰 발급 수가 최대치를 초과

        when(couponRepository.findById(anyLong())).thenReturn(Optional.of(coupon));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            couponService.issueCoupon(1L, user);
        });

        assertEquals("쿠폰이 소진되었습니다.", exception.getMessage());
    }

    @Test
    void testIssueCouponWhenCouponNotFound() {
        when(couponRepository.findById(anyLong())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            couponService.issueCoupon(1L, user);
        });

        assertEquals("Coupon not found", exception.getMessage());
    }
}
