package hhplus.newgeniee.ecommerce.product.api.controller;

import hhplus.newgeniee.ecommerce.global.CommonApiResponse;
import hhplus.newgeniee.ecommerce.global.CommonApiResponses;
import hhplus.newgeniee.ecommerce.global.api.ApiFailResponse;
import hhplus.newgeniee.ecommerce.global.api.ApiSuccessResponse;
import hhplus.newgeniee.ecommerce.product.api.request.ProductSearchApiRequest;
import hhplus.newgeniee.ecommerce.product.api.response.BestProductApiResponse;
import hhplus.newgeniee.ecommerce.product.api.response.ProductApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


/**
 * API 컨트롤러 인터페이스
 */
@Tag(
        name = "상품 API"
)
public interface IProductApiController {
    @ApiSuccessResponse(
            value = ProductApiResponse.class,
            isList = true
    )
    @Operation(
            summary = "상품 목록 조회",
            description = "상품 목록을 조회한다.",
            parameters = {@Parameter(
                    name = "상품 조회 조건",
                    in = ParameterIn.QUERY,
                    example = "name=상품1"
            ), @Parameter(
                    name = "페이지 조건",
                    in = ParameterIn.QUERY,
                    example = "page=1&size=20"
            )}
    )
    @GetMapping
    CommonApiResponses<ProductApiResponse> getProducts(@ModelAttribute final ProductSearchApiRequest request, final Pageable pageable);

    @ApiFailResponse("상품을 찾을 수 없습니다.")
    @ApiSuccessResponse(ProductApiResponse.class)
    @Operation(
            summary = "상품 단건 조회",
            description = "상품 아이디로 상품을 조회한다.",
            parameters = {@Parameter(
                    name = "상품 아이디",
                    in = ParameterIn.PATH,
                    required = true,
                    example = "1"
            )}
    )
    @GetMapping({"/{productId}"})
    CommonApiResponse<ProductApiResponse> getProduct(@PathVariable final Long productId);

    @ApiSuccessResponse(BestProductApiResponse.class)
    @Operation(
            summary = "베스트 상품 조회",
            description = "베스트 상품을 조회한다."
    )
    @GetMapping({"/best"})
    CommonApiResponse<List<BestProductApiResponse>> getBestProducts();
}
