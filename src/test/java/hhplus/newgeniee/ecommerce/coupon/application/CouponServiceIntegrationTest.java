package hhplus.newgeniee.ecommerce.coupon.application;

import hhplus.newgeniee.ecommerce.common.ServiceIntegrationTest;
import hhplus.newgeniee.ecommerce.coupon.api.request.CouponIssueRequest;
import hhplus.newgeniee.ecommerce.coupon.api.response.CouponResponse;
import hhplus.newgeniee.ecommerce.coupon.api.response.IssuedCouponResponse;
import hhplus.newgeniee.ecommerce.coupon.domain.Coupon;
import hhplus.newgeniee.ecommerce.coupon.domain.CouponQuantity;
import hhplus.newgeniee.ecommerce.coupon.domain.DiscountType;
import hhplus.newgeniee.ecommerce.user.domain.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponServiceIntegrationTest extends ServiceIntegrationTest {

    @DisplayName("쿠폰 단건 조회")
    @Nested
    class getCoupon {

        @DisplayName("쿠폰 아이디로 쿠폰을 조회할 수 있다.")
        @Test
        void getCouponById() throws Exception {
            // given
            final String name = "쿠폰1";
            final int issueLimit = 30;
            final int quantity = 30;
            final int discountValue = 1000;

            final Coupon savedCoupon = couponJpaRepository.save(Coupon.builder()
                    .name(name)
                    .issueLimit(issueLimit)
                    .quantity(quantity)
                    .discountType(DiscountType.FIXED)
                    .discountValue(discountValue)
                    .expiredAt(LocalDateTime.now().plusDays(1))
                    .build());

            // when
            final CouponResponse result = couponService.getCoupon(savedCoupon.getId());

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

        @DisplayName("조회하려는 쿠폰 아이디가 음수면 IllegalArgumentException 발생한다.")
        @Test
        void getCouponByNegativeId() {
            // given
            final Long id = -1L;

            // when & then
            assertThatThrownBy(() -> couponService.getCoupon(id))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("쿠폰 ID는 0보다 큰 값이어야 합니다.");
        }

        @DisplayName("쿠폰 아이디로 조회할 때 쿠폰이 존재하지 않으면 EntityNotFoundException 발생한다.")
        @Test
        void getCouponByIdNotFound() {
            // given
            final Long id = 1L;

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
            final String userName = "사용자1";
            final User user = userJpaRepository.save(User.builder().name(userName).build());

            final String couponName = "쿠폰1";
            final int issueLimit = 30;
            final int quantity = 30;
            final int discountValue = 1000;

            final Coupon coupon = couponJpaRepository.save(Coupon.builder()
                    .name(couponName)
                    .issueLimit(issueLimit)
                    .quantity(quantity)
                    .discountType(DiscountType.FIXED)
                    .discountValue(discountValue)
                    .expiredAt(LocalDateTime.now().plusDays(1))
                    .build());

            final CouponQuantity couponQuantity = couponQuantityJpaRepository.save(CouponQuantity.builder()
                    .couponId(coupon.getId())
                    .quantity(quantity)
                    .build());

            final CouponIssueRequest request = CouponIssueRequest.builder()
                    .couponId(coupon.getId())
                    .userId(user.getId())
                    .build();

            // when
            final IssuedCouponResponse result = couponService.issueCoupon(request);

            // then
            assertThat(result).isNotNull()
                    .extracting("couponId", "name", "discountType", "discountValue")
                    .containsExactly(coupon.getId(), couponName, DiscountType.FIXED.name(), discountValue);

            assertThat(coupon.getQuantity()).isEqualTo(couponQuantity.getQuantity());
        }

        @DisplayName("유효하지 않은 사용자 아이디로 요청하면 쿠폰 발급을 요청하면 EntityNotFoundException 예외가 발생한다.")
        @Test
        void issueWithNotFoundUser() {
            // given
            final Coupon coupon = couponJpaRepository.save(Coupon.builder()
                    .name("쿠폰1")
                    .issueLimit(30)
                    .quantity(30)
                    .discountType(DiscountType.FIXED)
                    .discountValue(1000)
                    .expiredAt(LocalDateTime.now().plusDays(1))
                    .build());

            final CouponIssueRequest request = CouponIssueRequest.builder()
                    .couponId(coupon.getId())
                    .userId(System.currentTimeMillis())
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
            final User user = userJpaRepository.save(User.builder().name("사용자1").build());

            final CouponIssueRequest request = CouponIssueRequest.builder()
                    .userId(user.getId())
                    .couponId(System.currentTimeMillis())
                    .build();

            // when & then
            assertThatThrownBy(() -> couponService.issueCoupon(request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("사용자를 찾을 수 없습니다.");
        }

        @DisplayName("유효하지 않은 쿠폰 수량 아이디로 요청하면 쿠폰 발급을 요청하면 EntityNotFoundException 예외가 발생한다.")
        @Test
        void issueWithNotFoundCouponQuantity() {
            // given
            final User user = userJpaRepository.save(User.builder().name("사용자1").build());
            final Coupon coupon = couponJpaRepository.save(Coupon.builder()
                    .name("쿠폰1")
                    .issueLimit(30)
                    .quantity(30)
                    .discountType(DiscountType.FIXED)
                    .discountValue(1000)
                    .expiredAt(LocalDateTime.now().plusDays(1))
                    .build());

            final CouponIssueRequest request = CouponIssueRequest.builder()
                    .userId(user.getId())
                    .couponId(coupon.getId())
                    .build();

            // when & then
            assertThatThrownBy(() -> couponService.issueCoupon(request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("쿠폰을 찾을 수 없습니다.");
        }

        @DisplayName("발급 가능한 쿠폰이 0이하시 발급요청을 하면 IllegalArgumentException 예외가 발생한다.")
        @Test
        void issueCouponQuantityNotEnough() throws Exception {
            // given
            final String userName = "사용자1";
            final User user = userJpaRepository.save(User.builder().name(userName).build());

            final String couponName = "쿠폰1";
            final int issueLimit = 30;
            final int quantity = 0;

            final Coupon coupon = couponJpaRepository.save(Coupon.builder()
                    .name(couponName)
                    .issueLimit(issueLimit)
                    .quantity(quantity)
                    .discountType(DiscountType.FIXED)
                    .discountValue(1000)
                    .expiredAt(LocalDateTime.now().plusDays(1))
                    .build());

            couponQuantityJpaRepository.save(CouponQuantity.builder()
                    .couponId(coupon.getId())
                    .quantity(quantity)
                    .build());

            final CouponIssueRequest request = CouponIssueRequest.builder()
                    .userId(user.getId())
                    .couponId(coupon.getId())
                    .build();

            // when & then
            assertThatThrownBy(() -> couponService.issueCoupon(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("쿠폰 수량이 부족합니다.");
        }
    }


    @DisplayName("분산 락을 사용한 쿠폰 발급 동시성 테스트")
    @Nested
    class DistributedLockCouponIssueTest extends ServiceIntegrationTest {

        @DisplayName("동시 요청 시 분산 락을 통해 하나의 요청만 발급된다.")
        @Test
        void issueCouponWithDistributedLock() throws InterruptedException, ExecutionException {
            // given
            final String userName = "사용자1";
            final User user = userJpaRepository.save(User.builder().name(userName).build());

            final String couponName = "쿠폰1";
            final int issueLimit = 30;
            final int quantity = 30;
            final int discountValue = 1000;

            final Coupon coupon = couponJpaRepository.save(Coupon.builder()
                    .name(couponName)
                    .issueLimit(issueLimit)
                    .quantity(quantity)
                    .discountType(DiscountType.FIXED)
                    .discountValue(discountValue)
                    .expiredAt(LocalDateTime.now().plusDays(1))
                    .build());

            couponQuantityJpaRepository.save(CouponQuantity.builder()
                    .couponId(coupon.getId())
                    .quantity(quantity)
                    .build());

            final CouponIssueRequest request = CouponIssueRequest.builder()
                    .couponId(coupon.getId())
                    .userId(user.getId())
                    .build();

            // when
            int threadCount = 10; // 동시 요청 수
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            // 여러 스레드에서 동시에 쿠폰 발급 요청
            List<Future<IssuedCouponResponse>> futures = new ArrayList<>();
            for (int i = 0; i < threadCount; i++) {
                futures.add(executorService.submit(() -> {
                    try {
                        return couponService.issueCoupon(request);
                    } finally {
                        latch.countDown();
                    }
                }));
            }

            // all threads complete
            latch.await();

            // then
            // 쿠폰 발급은 한 번만 성공해야 한다
            long issuedCount = issuedCouponJpaRepository.countByCouponId(coupon.getId());
            assertThat(issuedCount).isEqualTo(1); // 쿠폰은 하나만 발급되어야 한다.
            executorService.shutdown();
        }
    }
}
