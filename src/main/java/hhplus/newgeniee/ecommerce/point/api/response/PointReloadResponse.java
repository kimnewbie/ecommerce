package hhplus.newgeniee.ecommerce.point.api.response;

import hhplus.newgeniee.ecommerce.point.domain.Point;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 포인트 충전 API 응답 DTO 클래스
 */
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PointReloadResponse {

    private final int point;

    public static PointReloadResponse from(final Point point) {
        return PointReloadResponse.builder()
                .point(point.getPoint())
                .build();
    }
}