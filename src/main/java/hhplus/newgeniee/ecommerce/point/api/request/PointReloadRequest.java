package hhplus.newgeniee.ecommerce.point.api.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 포인트 충전 API 요청 DTO 클래스
 */
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PointReloadRequest {

    private final Long userId;
    private final int reloadPoint;
}
