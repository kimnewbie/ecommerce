package hhplus.newgeniee.ecommerce.order.application;

import hhplus.newgeniee.ecommerce.order.api.request.OrderCreateRequest;
import hhplus.newgeniee.ecommerce.order.api.response.OrderCreateResponse;
import hhplus.newgeniee.ecommerce.order.domain.Order;
import hhplus.newgeniee.ecommerce.order.domain.OrderItem;
import hhplus.newgeniee.ecommerce.order.domain.OrderItemRepository;
import hhplus.newgeniee.ecommerce.order.domain.OrderRepository;
import hhplus.newgeniee.ecommerce.product.domain.*;
import hhplus.newgeniee.ecommerce.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    private final UserRepository userRepository;

    private final StockService stockService;

    @Transactional
    public OrderCreateResponse order(final OrderCreateRequest request) {
        if (!userRepository.existsById(request.getUserId())) {
            throw new NoSuchElementException("사용자를 찾을 수 없습니다.");
        }

        Order order = orderRepository.save(Order.createOrder(request.getUserId()));

        final List<Long> productIds = request.extractProductIds();
        final List<Product> products = productRepository.getAllById(productIds);
        if (products.size() != productIds.size()) {
            throw new NoSuchElementException("상품을 찾을 수 없습니다.");
        }
        final Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        request.getOrderItems()
                .forEach(orderItem -> {

                    final Product product = productMap.get(orderItem.getProductId());
                    final Stock stock = stockRepository.findByProductIdForUpdate(product.getId())
                            .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));

                    stockService.decrease(product, stock, orderItem.getQuantity());

                    final OrderItem item = orderItemRepository.save(OrderItem.builder()
                            .orderId(order.getId())
                            .productId(product.getId())
                            .productName(product.getName())
                            .quantity(orderItem.getQuantity())
                            .price(product.getPrice())
                            .build());
                    order.addOrderItem(item);
                });
        return OrderCreateResponse.from(order);
    }
}
