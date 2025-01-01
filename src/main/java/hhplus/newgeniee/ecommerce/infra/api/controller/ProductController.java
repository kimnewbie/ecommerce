package hhplus.newgeniee.ecommerce.infra.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/product")
@Tag(name = "Product API", description = "상품 관련 API")
public class ProductController {

    // product dummy 데이터를 만들기 위해서
    static class Product {
        private String name;
        private int totalSalesQty;

        public Product(String name, int totalSalesQty) {
            this.name = name;
            this.totalSalesQty = totalSalesQty;
        }

        public String getName() {
            return name;
        }

        public int getTotalSalesQty() {
            return totalSalesQty;
        }
    }

    @GetMapping("/list")
    @Operation(
            summary = "모든 상품 조회",
            description = "현재 등록된 모든 상품의 목록을 조회",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 상품 목록을 반환"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    public ResponseEntity<List<String>> getProducts() {
        // 더미 데이터로 상품 목록 반환
        List<String> products = List.of("Product A", "Product B", "Product C");
        return ResponseEntity.ok(products);
    }

    @GetMapping("/list/{product_id}")
    @Operation(
            summary = "상품 조회",
            description = "ID를 기반으로 특정 상품을 조회",
            parameters = {
                    @Parameter(
                            name = "product_id",
                            description = "조회할 상품의 ID",
                            required = true,
                            example = "1"
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 상품을 반환"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    public ResponseEntity<String> getProductById(@PathVariable Long product_id) {
        // 더미 데이터로 상품 조회
        String product = "Product with ID: " + product_id;
        return ResponseEntity.ok(product);
    }

    @Operation(
            summary = "인기 상품 조회",
            description = "판매량 기준 상위 인기 상품을 조회",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 인기 상품 목록을 반환"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @GetMapping("/top")
    public ResponseEntity<List<String>> getTopProducts() {
        // 5개 이상의 데이터 생성
        List<Product> products = List.of(
                new Product("Product A", 1),
                new Product("Product B", 2),
                new Product("Product C", 3),
                new Product("Product D", 4),
                new Product("Product E", 5),
                new Product("Product F", 6),
                new Product("Product G", 7)
        );

        List<String> topProducts = products.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getTotalSalesQty(), p1.getTotalSalesQty()))
                .limit(5)
                .map(Product::getName)
                .collect(Collectors.toList());

        return ResponseEntity.ok(topProducts);
    }
}
