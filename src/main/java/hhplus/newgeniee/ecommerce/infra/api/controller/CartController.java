package hhplus.newgeniee.ecommerce.infra.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


// TODO. uuid 사용해서 사용자 인증해야 한다

@RestController
@AllArgsConstructor
@RequestMapping("/cart")
@Tag(name = "Cart API", description = "장바구니 관련 API")
public class CartController {

    // CartItem 객체
    @AllArgsConstructor
    @Getter
    @Setter
    static class CartItem {
        private Long cartId;
        private Long productId;
        private Long userId;
        private int orderQty;
        private LocalDateTime createdAt;
    }

    // 더미 데이터 (CART 테이블에 맞춘 데이터)
    private static final List<CartItem> cartItems = new ArrayList<>();

    static {
        // 더미 데이터 삽입
        cartItems.add(new CartItem(1L, 101L, 1L, 2, LocalDateTime.of(2025, 1, 1, 10, 0, 0, 0)));
        cartItems.add(new CartItem(2L, 102L, 1L, 1, LocalDateTime.of(2025, 1, 1, 10, 0, 0, 0)));
        cartItems.add(new CartItem(3L, 103L, 2L, 3, LocalDateTime.of(2025, 1, 1, 10, 0, 0, 0)));
        cartItems.add(new CartItem(4L, 104L, 3L, 1, LocalDateTime.of(2025, 1, 1, 10, 0, 0, 0)));
        cartItems.add(new CartItem(5L, 105L, 2L, 2, LocalDateTime.of(2025, 1, 1, 10, 0, 0, 0)));
    }

    // 장바구니에 담긴 상품 조회
    @Operation(
            summary = "장바구니에 담긴 상품 조회",
            description = "장바구니에 담긴 상품을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 상품 목록을 반환"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @GetMapping
    public ResponseEntity<Object> getCartItems() {
        if (cartItems.isEmpty()) {
            return ResponseEntity.status(204).body("장바구니에 담긴 상품이 없습니다."); // 장바구니가 비었을 경우 204 No Content와 메시지
        }
        return ResponseEntity.ok(cartItems); // 장바구니 목록을 반환
    }

    // 장바구니에 상품 추가
    @Operation(
            summary = "장바구니에 특정 상품을 추가",
            description = "장바구니에 특정 상품을 추가합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "상품이 성공적으로 추가"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @PostMapping("/add")
    public ResponseEntity<CartItem> addToCart(@RequestParam Long productId, @RequestParam Long userId, @RequestParam int orderQty) {
        // Find existing item with the same productId and userId
        CartItem existingItem = cartItems.stream()
                .filter(item -> item.getProductId().equals(productId) && item.getUserId().equals(userId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // If item exists, update orderQty
            existingItem.setOrderQty(existingItem.getOrderQty() + orderQty);
        } else {
            // If item does not exist, create a new CartItem and add it to the cart
            CartItem newItem = new CartItem((long) (cartItems.size() + 1), productId, userId, orderQty, LocalDateTime.now());
            cartItems.add(newItem);
            existingItem = newItem;
        }

        return ResponseEntity.status(201).body(existingItem);
    }

    // 장바구니에서 상품 제거
    @Operation(
            summary = "장바구니에서 특정 상품 제거",
            description = "장바구니에서 특정 상품을 제거합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "상품이 성공적으로 제거"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @DeleteMapping("/remove")
    public ResponseEntity<Long> removeFromCart(@RequestParam Long cartId) {
        boolean removed = cartItems.removeIf(item -> item.getCartId().equals(cartId));
        if (removed) {
            return ResponseEntity.status(200).body(cartId); // 삭제된 cartId 반환
        } else {
            return ResponseEntity.status(404).body(null); // 상품을 찾을 수 없으면 null 반환
        }
    }
}
