package hhplus.newgeniee.ecommerce.point.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 포인트 충전 응답 DTO
 */
@Schema(description = "포인트 충전 응답")
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PointReloadApiResponse {

    @Schema(description = "충전 후 포인트", example = "10000")
    private final int point;

    public static PointReloadApiResponse from(final PointReloadResponse reloadResponse) {
        return PointReloadApiResponse.builder()
                .point(reloadResponse.getPoint())
                .build();
    }
}