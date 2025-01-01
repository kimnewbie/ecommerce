package hhplus.newgeniee.ecommerce.infra.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/order")
@Tag(name = "Order API", description = "주문 관련 API")
public class OrderController {

    private static final List<User> users = List.of(
            new User(1L, 10000L),
            new User(2L, 5000L)
    );

    private static final List<Product> products = List.of(
            new Product(101L, "Product A", 2000L),
            new Product(102L, "Product B", 3000L)
    );

    private static final List<Order> orders = new java.util.ArrayList<>();

    // 더미 주문 데이터 추가 (Mock)
    static {
        // 사용자 1이 주문한 예시
        Order order1 = new Order(1L, 5000L, true, List.of(
                new OrderItem(101L, 2),  // Product A, 2개
                new OrderItem(102L, 1)   // Product B, 1개
        ));
        orders.add(order1);

        // 사용자 2가 주문한 예시
        Order order2 = new Order(2L, 8000L, true, List.of(
                new OrderItem(101L, 3),  // Product A, 3개
                new OrderItem(102L, 1)   // Product B, 1개
        ));
        orders.add(order2);

        // 사용자 1이 결제되지 않은 주문을 한 예시
        Order order3 = new Order(1L, 3000L, false, List.of(
                new OrderItem(101L, 1)   // Product A, 1개
        ));
        orders.add(order3);
    }

    // 주문 처리
    @Operation(
            summary = "주문 및 결제 수행",
            description = "사용자 식별자와 상품 ID, 수량을 입력받아 주문하고 결제를 수행합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "결제 성공 및 주문 정보 반환"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @PostMapping("/place")
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest orderRequest) {
        // 사용자의 잔액 확인
        User user = users.stream()
                .filter(u -> u.getUserId().equals(orderRequest.getUserId()))
                .findFirst()
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
        }

        // 상품 목록에서 가격 계산 및 유효성 검사
        long totalAmount = 0;
        for (OrderItem item : orderRequest.getItems()) {
            if (item.getProductId() == null || item.getProductId() <= 0) {
                return ResponseEntity.badRequest().body("유효하지 않은 상품 ID: " + item.getProductId());
            }
            if (item.getQuantity() <= 0) {  // int 타입이므로 null 확인은 불필요
                return ResponseEntity.badRequest().body("유효하지 않은 수량: " + item.getQuantity());
            }

            // 상품 목록에서 가격을 찾아서 총 금액 계산
            long itemTotal = products.stream()
                    .filter(p -> p.getProductId().equals(item.getProductId()))
                    .findFirst()
                    .map(product -> product.getPrice() * item.getQuantity())
                    .orElse(0L);

            totalAmount += itemTotal;
        }

        // 잔액 확인
        if (user.getBalance() < totalAmount) {
            return ResponseEntity.badRequest().body("잔액이 부족합니다.");
        }

        // 결제 성공: 잔액 차감
        user.setBalance(user.getBalance() - totalAmount);

        // 외부 데이터 플랫폼에 주문 정보 전송 (Mock)
        boolean sendOrder = sendOrderToDataPlatform(orderRequest);

        // 외부 데이터 플랫폼 전송 성공
        if (sendOrder) {
            // 주문 정보 Mock 저장
            Order order = new Order(orderRequest.getUserId(), totalAmount, true, orderRequest.getItems());
            orders.add(order);
            return ResponseEntity.ok("주문 및 결제가 성공적으로 처리되었습니다.");
        } else {
            // 외부 시스템에 주문 전송 실패
            return ResponseEntity.status(500).body("주문 정보를 데이터 플랫폼으로 전송하는데 실패했습니다.");
        }
    }

    // 주문 취소 API
    @Operation(
            summary = "주문 취소",
            description = "사용자가 결제된 주문을 취소합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "주문 취소 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @PostMapping("/cancel")
    public ResponseEntity<String> cancelOrder(@RequestBody OrderCancelRequest cancelRequest) {
        // 주문 존재 여부 확인
        Optional<Order> orderOptional = orders.stream()
                .filter(order -> order.getUserId().equals(cancelRequest.getUserId()) &&
                        order.getOrderId().equals(cancelRequest.getOrderId()))
                .findFirst();

        if (!orderOptional.isPresent()) {
            return ResponseEntity.status(404).body("주문을 찾을 수 없습니다.");
        }

        Order order = orderOptional.get();

        // 결제 상태 확인
        if (!order.isPaid()) {
            return ResponseEntity.status(400).body("결제가 완료되지 않은 주문은 취소할 수 없습니다.");
        }

        // 결제 환불 처리 (Mock)
        boolean paymentRefunded = refundPayment(order.getTotalAmount());
        if (!paymentRefunded) {
            return ResponseEntity.status(500).body("결제 환불에 실패했습니다.");
        }

        // 주문 취소 처리 (Mock)
        order.setPaid(false);  // 결제 상태를 취소로 설정
        return ResponseEntity.ok("주문이 성공적으로 취소되었습니다.");
    }

    // 결제 환불 처리 (Mock)
    private boolean refundPayment(long amount) {
        try {
            System.out.println("환불 요청: 금액 = " + amount);
            return true; // 실제로는 외부 시스템과 연동하여 환불을 처리해야 함
        } catch (Exception e) {
            System.err.println("환불 처리 실패: " + e.getMessage());
            return false;
        }
    }

    // 외부 데이터 플랫폼에 주문 정보 전송
    private boolean sendOrderToDataPlatform(OrderRequest orderRequest) {
        try {
            // 외부 시스템에 주문 데이터를 전송하는 로직 (HTTP 요청 등)
            System.out.println("데이터 플랫폼에 주문 전송: " + orderRequest);
            return true;  // 실제 API 호출 성공 시 true
        } catch (Exception e) {
            // 예외 발생 시 실패 처리
            System.err.println("주문 전송 실패: " + e.getMessage());
            return false;  // 실패 시 false 반환
        }
    }

    // 사용자의 주문 목록 조회
    @Operation(
            summary = "사용자 주문 목록 조회",
            description = "사용자의 주문 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "주문 목록 반환"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @GetMapping("/{userId}")
    public ResponseEntity<List<Order>> getOrders(@PathVariable Long userId) {
        // 사용자가 존재하는지 확인
        User user = users.stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).body(null);  // 사용자 없음
        }

        // 해당 사용자의 주문 목록 반환
        List<Order> userOrders = orders.stream()
                .filter(order -> order.getUserId().equals(userId))
                .collect(Collectors.toList());

        return ResponseEntity.ok(userOrders);  // 주문 목록 반환
    }

    // 사용자 객체 (잔액 포함)
    @Getter
    @Setter
    @AllArgsConstructor
    static class User {
        private Long userId;
        private Long balance;
    }

    // 상품 객체
    @Getter
    @AllArgsConstructor
    static class Product {
        private Long productId;
        private String name;
        private Long price;
    }

    // 주문 요청 객체 (상품 목록 포함)
    @Getter
    @AllArgsConstructor
    static class OrderRequest {
        private Long userId;
        private List<OrderItem> items;
    }

    // 주문 항목 객체 (상품 ID와 수량 포함)
    @Getter
    @AllArgsConstructor
    static class OrderItem {
        private Long productId;
        private int quantity;
    }

    // 주문 취소 요청 객체
    @Getter
    @Setter
    static class OrderCancelRequest {
        private Long userId;
        private Long orderId;
    }

    // 주문 객체
    @Getter
    @Setter
    static class Order {
        private Long orderId;
        private Long userId;
        private long totalAmount;
        private boolean paid;
        private List<OrderItem> items;

        public Order(Long userId, long totalAmount, boolean paid, List<OrderItem> items) {
            this.orderId = System.currentTimeMillis();  // 주문 ID는 현재 시간으로 생성 (임의)
            this.userId = userId;
            this.totalAmount = totalAmount;
            this.paid = paid;
            this.items = items;
        }
    }
}
