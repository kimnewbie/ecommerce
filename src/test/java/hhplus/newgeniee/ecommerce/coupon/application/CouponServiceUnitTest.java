package hhplus.newgeniee.ecommerce.coupon.application;

import hhplus.newgeniee.ecommerce.coupon.api.request.CouponIssueRequest;
import hhplus.newgeniee.ecommerce.coupon.api.response.CouponResponse;
import hhplus.newgeniee.ecommerce.coupon.api.response.IssuedCouponResponse;
import hhplus.newgeniee.ecommerce.coupon.domain.*;
import hhplus.newgeniee.ecommerce.user.domain.User;
import hhplus.newgeniee.ecommerce.user.domain.UserRepository;
import hhplus.newgeniee.ecommerce.util.EntityIdSetter;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
class CouponServiceUnitTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponQuantityRepository couponQuantityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CouponIssuer couponIssuer;

    @Mock
    private IssuedCouponRepository issuedCouponRepository;

    @DisplayName("쿠폰 단건 조회")
    @Nested
    class getCoupon {

        @DisplayName("쿠폰 아이디로 쿠폰을 조회할 수 있다.")
        @Test
        void getCouponById() throws Exception {
            // given
            final Long couponId = 1L;

            final String name = "쿠폰1";
            final int issueLimit = 30;
            final int quantity = 30;
            final int discountValue = 1000;

            final Coupon coupon = Coupon.builder()
                    .name(name)
                    .issueLimit(issueLimit)
                    .quantity(quantity)
                    .discountType(DiscountType.FIXED)
                    .discountValue(discountValue)
                    .build();

            EntityIdSetter.setId(coupon, couponId);
            given(couponRepository.findById(couponId))
                    .willReturn(Optional.of(coupon));

            // when
            final CouponResponse result = couponService.getCoupon(couponId);

            // then
            assertThat(result).isNotNull()
                    .extracting("name", "issueLimit", "quantity", "discountValue")
                    .containsExactly(name, issueLimit, quantity, discountValue);
        }

        @DisplayName("조회하려는 쿠폰 아이디가 Null이면 IllegalArgumentException 발생한다.")
        @Test
        void getCouponByNullId() throws Exception {
            // given
            final Long id = null;

            // when & then
            assertThatThrownBy(() -> couponService.getCoupon(id))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("쿠폰 ID는 0보다 큰 값이어야 합니다.");
        }

        @DisplayName("조회하려는 쿠폰 아이디가 0이면 IllegalArgumentException 발생한다.")
        @Test
        void getCouponByZeroId() {
            // given
            final Long id = 0L;

            // when & then
            assertThatThrownBy(() -> couponService.getCoupon(id))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("쿠폰 ID는 0보다 큰 값이어야 합니다.");
        }

        @DisplayName("조회하려는 쿠폰 아이디가 음수면 IllegalArgumentException이 발생한다.")
        @Test
        void getCouponByNegativeId() {
            // given
            final Long id = -1L;

            // when & then
            assertThatThrownBy(() -> couponService.getCoupon(id))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("쿠폰 ID는 0보다 큰 값이어야 합니다.");
        }

        @DisplayName("쿠폰 아이디로 조회할 때 쿠폰이 존재하지 않으면 EntityNotFoundException이 발생한다.")
        @Test
        void getCouponByIdNotFound() {
            // given
            final Long id = 1L;

            given(couponRepository.findById(id))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> couponService.getCoupon(id))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("쿠폰을 찾을 수 없습니다.");
        }
    }

    @DisplayName("쿠폰 발급")
    @Nested
    class issueCoupon {

        @DisplayName("쿠폰 발급을 성공한다.")
        @Test
        void issueCoupon() throws Exception {
            // given
            final Long userId = 1L;
            final User user = User.builder()
                    .name("홍길동")
                    .build();

            given(userRepository.findById(userId))
                    .willReturn(Optional.of(user));

            final String couponName = "쿠폰1";
            final int issueLimit = 30;
            final int quantity = 30;
            final int discountValue = 1000;

            final Long couponId = 1L;
            final Coupon coupon = Coupon.builder()
                    .name(couponName)
                    .issueLimit(issueLimit)
                    .quantity(quantity)
                    .discountType(DiscountType.FIXED)
                    .discountValue(discountValue)
                    .build();

            EntityIdSetter.setId(coupon, couponId);
            given(couponRepository.findById(anyLong()))
                    .willReturn(Optional.of(coupon));

            final CouponQuantity couponQuantity = CouponQuantity.builder()
                    .couponId(couponId)
                    .quantity(quantity)
                    .build();

            given(couponQuantityRepository.findByCouponIdForUpdate(couponId))
                    .willReturn(Optional.of(couponQuantity));

            final CouponIssueRequest request = CouponIssueRequest.builder()
                    .couponId(couponId)
                    .userId(userId)
                    .build();

            final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
                    .userId(user.getId())
                    .couponId(coupon.getId())
                    .discountType(DiscountType.FIXED)
                    .discountValue(discountValue)
                    .expiredAt(coupon.getExpiredAt())
                    .build();

            given(couponIssuer.issue(user, coupon, couponQuantity))
                    .willReturn(issuedCoupon);

            given(issuedCouponRepository.save(issuedCoupon))
                    .willReturn(issuedCoupon);

            // when
            final IssuedCouponResponse result = couponService.issueCoupon(request);

            // then
            assertThat(result).isNotNull()
                    .extracting("couponId", "name", "discountValue")
                    .containsExactly(coupon.getId(), couponName, discountValue);

            assertThat(coupon.getQuantity()).isEqualTo(couponQuantity.getQuantity());
        }

        @DisplayName("유효하지 않은 사용자 아이디로 요청하면 쿠폰 발급을 요청하면 EntityNotFoundException 예외가 발생한다.")
        @Test
        void issueWithNotFoundUser() {
            // given
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            final CouponIssueRequest request = CouponIssueRequest.builder()
                    .couponId(1L)
                    .userId(1L)
                    .build();

            // when & then
            assertThatThrownBy(() -> couponService.issueCoupon(request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("사용자를 찾을 수 없습니다.");
        }

        @DisplayName("유효하지 않은 쿠폰 아이디로 요청하면 쿠폰 발급을 요청하면 EntityNotFoundException 예외가 발생한다.")
        @Test
        void issueWithNotFoundCoupon() {
            // given
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.of(User.builder().build()));

            given(couponRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            final CouponIssueRequest request = CouponIssueRequest.builder()
                    .couponId(1L)
                    .userId(1L)
                    .build();

            // when & then
            assertThatThrownBy(() -> couponService.issueCoupon(request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("사용자를 찾을 수 없습니다.");
        }

        @DisplayName("쿠폰 발급 가능 수량을 초과해서 발급요청을 하면 IllegalArgumentException 예외가 발생한다.")
        @Test
        void issueOverLimit() {
            // given
            final User user = User.builder().build();
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.of(user));

            final long couponId = 1L;
            final int quantity = 0;
            final Coupon coupon = Coupon.builder()
                    .quantity(quantity)
                    .build();

            EntityIdSetter.setId(coupon, couponId);
            given(couponRepository.findById(anyLong()))
                    .willReturn(Optional.of(coupon));

            final CouponQuantity couponQuantity = CouponQuantity.builder()
                    .couponId(couponId)
                    .quantity(quantity)
                    .build();
            given(couponQuantityRepository.findByCouponIdForUpdate(anyLong()))
                    .willReturn(Optional.of(couponQuantity));

            willThrow(new IllegalArgumentException("쿠폰 수량이 부족합니다."))
                    .given(couponIssuer)
                    .issue(user, coupon, couponQuantity);

            final CouponIssueRequest request = CouponIssueRequest.builder()
                    .userId(1L)
                    .couponId(1L)
                    .build();

            // when & then
            assertThatThrownBy(() -> couponService.issueCoupon(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("쿠폰 수량이 부족합니다.");
        }
    }
}
