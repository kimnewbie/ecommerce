package hhplus.newgeniee.ecommerce.infra.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/coupon")
public class CouponController {

    @Getter
    @AllArgsConstructor
    static class Coupon {
        private Long couponId;
        private double discountRate;
        private String status;
        private int issuedCount;
        private int maxIssuableCount;
        private LocalDateTime expirationAt;

        // 사용 가능한 쿠폰인지 확인
        public boolean isValidForIssuance() {
            return "ISSUED".equals(status) &&
                    maxIssuableCount > issuedCount &&
                    expirationAt.isAfter(LocalDateTime.now());
        }
    }

    public List<Coupon> coupons;


    // 사용자별 쿠폰 발급 기록 (Map 사용)
    private Map<Long, Set<Long>> couponHistory;  // userId -> 발급된 couponIds

    @PostConstruct
    public void init() {
        coupons = new ArrayList<>(List.of(
                new Coupon(1L, 0.10, "ISSUED", 10, 30, LocalDateTime.now().plusDays(7)),  // 유효
                new Coupon(2L, 0.15, "PENDING", 5, 30, LocalDateTime.now().plusDays(3)),  // PENDING 상태 -> 무효
                new Coupon(3L, 0.20, "ISSUED", 30, 30, LocalDateTime.now().plusDays(1)), // 유효
                new Coupon(4L, 0.10, "PENDING", 0, 30, LocalDateTime.now().plusDays(1))   // PENDING 상태, 발급 가능 수 0 -> 무효
        ));

        // 쿠폰 발급 기록 초기화 (예시)
        couponHistory = new HashMap<>();
        couponHistory.put(1L, new HashSet<>(Set.of(1L))); // userId 1은 couponId 1을 발급받음
        couponHistory.put(2L, new HashSet<>(Set.of(3L))); // userId 2는 couponId 3을 발급받음
    }

    @GetMapping("/list/all")
    @Operation(
            summary = "쿠폰 전체 목록 조회",
            description = "전체 쿠폰 목록을 조회",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 쿠폰 목록을 반환"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    public List<Coupon> getCouponListAll() {
        return coupons;
    }

    @GetMapping("/list")
    @Operation(
            summary = "사용 가능한 쿠폰 목록 조회",
            description = "사용 가능한 쿠폰 목록을 조회",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 쿠폰 목록을 반환"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    public List<Coupon> getCouponList() {
        List<Coupon> validCoupons = coupons.stream()
                .filter(Coupon::isValidForIssuance)
                .collect(Collectors.toList());
        return validCoupons;
    }

    @PostMapping("/issue")
    @Operation(
            summary = "쿠폰 발급",
            description = "특정 userId와 couponId에 대해 쿠폰을 발급",
            responses = {
                    @ApiResponse(responseCode = "200", description = "쿠폰 발급 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "404", description = "사용자 또는 쿠폰을 찾을 수 없음")
            }
    )
    public String issueCoupon(
            @RequestParam(value = "userId", defaultValue = "1") Long userId,  // 기본값 설정
            @RequestParam(value = "couponId", defaultValue = "1") Long couponId // 기본값 설정
    ) {
        // 사용자가 이미 쿠폰을 발급받았는지 확인
        Set<Long> userCoupons = couponHistory.get(userId);
        if (userCoupons != null && userCoupons.contains(couponId)) {
            return "사용자는 이미 이 쿠폰을 발급받았습니다.";
        }

        // 유효한 쿠폰인지 확인
        Optional<Coupon> coupon = coupons.stream()
                .filter(c -> c.getCouponId().equals(couponId) && c.isValidForIssuance())
                .findFirst();

        if (coupon.isEmpty()) {
            return "유효한 쿠폰이 없습니다.";
        }

        // 사용자 쿠폰 발급 기록에 추가
        userCoupons = couponHistory.computeIfAbsent(userId, k -> new HashSet<>());
        userCoupons.add(couponId);

        return "쿠폰이 성공적으로 발급되었습니다.";
    }
}