package hhplus.newgeniee.ecommerce.product.api.controller;

import hhplus.newgeniee.ecommerce.global.CommonApiResponse;
import hhplus.newgeniee.ecommerce.global.CommonApiResponses;
import hhplus.newgeniee.ecommerce.product.api.request.ProductSearchApiRequest;
import hhplus.newgeniee.ecommerce.product.api.response.BestProductApiResponse;
import hhplus.newgeniee.ecommerce.product.api.response.BestProductResponse;
import hhplus.newgeniee.ecommerce.product.api.response.ProductApiResponse;
import hhplus.newgeniee.ecommerce.product.api.response.ProductResponse;
import hhplus.newgeniee.ecommerce.product.application.ProductService;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API 컨트롤러 구현체
 */

@RequestMapping({"/api/v1/products"})
@RestController
public class ProductApiController implements IProductApiController {

    @Generated
    private static final Logger log = LoggerFactory.getLogger(ProductApiController.class);
    private final ProductService productService;

    @GetMapping
    public CommonApiResponses<ProductApiResponse> getProducts(@ModelAttribute final ProductSearchApiRequest request, final Pageable pageable) {
        Page<ProductResponse> products = this.productService.getProducts(request.toServiceRequest(), pageable);
        return CommonApiResponses.ok(products, ProductApiResponse::from);
    }

    @GetMapping({"/{productId}"})
    public CommonApiResponse<ProductApiResponse> getProduct(@PathVariable final Long productId) {
        if (productId != null && productId > 0L) {
            ProductResponse product = this.productService.getProduct(productId);
            return CommonApiResponse.ok(ProductApiResponse.from(product));
        } else {
            throw new IllegalArgumentException("상품 ID는 1 이상의 값이어야 합니다.");
        }
    }

    @GetMapping({"/best"})
    public CommonApiResponse<List<BestProductApiResponse>> getBestProducts() {
        List<BestProductResponse> bestProducts = this.productService.getBestProducts();
        return CommonApiResponse.ok(bestProducts.stream().map(BestProductApiResponse::from).toList());
    }

    @Generated
    public ProductApiController(final ProductService productService) {
        this.productService = productService;
    }
}