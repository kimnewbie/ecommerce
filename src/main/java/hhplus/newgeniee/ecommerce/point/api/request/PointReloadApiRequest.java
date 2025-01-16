package hhplus.newgeniee.ecommerce.point.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 포인트 충전 요청 DTO
 */
@Schema(description = "포인트 충전 요청")
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PointReloadApiRequest {

    @Schema(description = "사용자 ID", example = "1")
    private final Long userId;

    @Schema(description = "충전 포인트 금액", example = "1000")
    private final int reloadPoint;

    public PointReloadRequest toServiceRequest() {
        return PointReloadRequest.builder()
                .userId(userId)
                .reloadPoint(reloadPoint)
                .build();
    }
}
