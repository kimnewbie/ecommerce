package hhplus.newgeniee.ecommerce.point.api.controller;

import hhplus.newgeniee.ecommerce.global.CommonApiResponse;
import hhplus.newgeniee.ecommerce.global.api.ApiFailResponse;
import hhplus.newgeniee.ecommerce.global.api.ApiSuccessResponse;
import hhplus.newgeniee.ecommerce.point.api.request.PointReloadApiRequest;
import hhplus.newgeniee.ecommerce.point.api.response.PointApiResponse;
import hhplus.newgeniee.ecommerce.point.api.response.PointReloadApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

/**
 * API 컨트롤러 인터페이스
 */
@Tag(name = "포인트 API")
public interface IPointApiController {

    @ApiSuccessResponse(PointApiResponse.class)
    @Operation(
            summary = "포인트 잔액 조회",
            description = "보유한 포인트 잔액을 조회한다.",
            parameters = {
                    @Parameter(name = "userId", description = "사용자 ID", in = QUERY, required = true, example = "1")
            }
    )
    @GetMapping
    CommonApiResponse<PointApiResponse> getPoint(
            @RequestParam final Long userId
    );

    @ApiFailResponse("충전 포인트는 1원 이상이어야 합니다.")
    @ApiSuccessResponse(PointReloadApiResponse.class)
    @Operation(
            summary = "포인트 충전",
            description = "포인트를 충전한다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = PointReloadApiRequest.class)
                    )
            )
    )
    @PostMapping("/charge")
    CommonApiResponse<PointReloadApiResponse> chargePoint(
            @RequestBody PointReloadApiRequest request
    );
}