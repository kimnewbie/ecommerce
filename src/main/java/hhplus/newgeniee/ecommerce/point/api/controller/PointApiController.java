package hhplus.newgeniee.ecommerce.point.api.controller;

import hhplus.newgeniee.ecommerce.global.CommonApiResponse;
import hhplus.newgeniee.ecommerce.point.api.request.PointReloadApiRequest;
import hhplus.newgeniee.ecommerce.point.api.response.PointApiResponse;
import hhplus.newgeniee.ecommerce.point.api.response.PointReloadApiResponse;
import hhplus.newgeniee.ecommerce.point.api.response.PointReloadResponse;
import hhplus.newgeniee.ecommerce.point.api.response.PointResponse;
import hhplus.newgeniee.ecommerce.point.application.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * API 컨트롤러 구현체
 */
@RequiredArgsConstructor
@RequestMapping("/api/v1/point")
@RestController
public class PointApiController implements IPointApiController {

    private final PointService pointService;

    @GetMapping
    @Override
    public CommonApiResponse<PointApiResponse> getPoint(@RequestParam final Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 요청입니다. 사용자 ID는 1 이상의 값이어야 합니다.");
        }
        PointResponse pointResponse = pointService.getPoint(userId);
        return CommonApiResponse.ok(PointApiResponse.from(pointResponse));
    }

    @PostMapping("/charge")
    @Override
    public CommonApiResponse<PointReloadApiResponse> chargePoint(@RequestBody final PointReloadApiRequest request) {
        final PointReloadResponse chargeResponse = pointService.reload(request.toServiceRequest());
        return CommonApiResponse.ok(PointReloadApiResponse.from(chargeResponse));
    }
}