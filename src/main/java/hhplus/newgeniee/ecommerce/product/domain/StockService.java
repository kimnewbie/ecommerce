package hhplus.newgeniee.ecommerce.product.domain;

import org.springframework.stereotype.Service;

@Service
public class StockService {

    //  차감 및 상품  수정
    public void decrease(final Product product, final Stock stock, final int quantity) {
        if (stock.getQuantity() < quantity) {
            throw new IllegalArgumentException("주문 수량이 현재 재고를 초과할 수 없습니다.");
        }
        stock.decrease(quantity);
        product.updateQuantity(stock.getQuantity());
    }
}