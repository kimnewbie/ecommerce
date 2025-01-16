package hhplus.newgeniee.ecommerce.coupon.api.controller;

import hhplus.newgeniee.ecommerce.coupon.api.request.CouponIssueApiRequest;
import hhplus.newgeniee.ecommerce.coupon.api.response.CouponApiResponse;
import hhplus.newgeniee.ecommerce.coupon.api.response.IssuedCouponApiResponse;
import hhplus.newgeniee.ecommerce.global.CommonApiResponse;
import hhplus.newgeniee.ecommerce.global.api.ApiFailResponse;
import hhplus.newgeniee.ecommerce.global.api.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;

@Tag(name = "쿠폰 API")
public interface ICouponApiController {

    @ApiFailResponse("쿠폰을 찾을 수 없습니다.")
    @ApiSuccessResponse(CouponApiResponse.class)
    @Operation(
            summary = "쿠폰 단건 조회",
            description = "쿠폰 아이디로 쿠폰을 조회한다.",
            parameters = {
                    @Parameter(name = "couponId", description = "쿠폰 ID", in = PATH, example = "1")
            }
    )
    @GetMapping("/{couponId}")
    CommonApiResponse<CouponApiResponse> getCoupon(@PathVariable Long couponId);

    @ApiFailResponse("유효하지 않은 쿠폰입니다.")
    @ApiSuccessResponse(IssuedCouponApiResponse.class)
    @Operation(
            summary = "쿠폰 발급",
            description = "사용자가 쿠폰 아이디로 쿠폰을 발급한다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = CouponIssueApiRequest.class)
                    )
            )
    )
    @PostMapping("/issue")
    CommonApiResponse<IssuedCouponApiResponse> issueCoupon(@RequestBody CouponIssueApiRequest issueRequest);
}
