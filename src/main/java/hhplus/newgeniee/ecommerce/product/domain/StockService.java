package hhplus.newgeniee.ecommerce.product.domain;

import org.springframework.stereotype.Service;

@Service
public class StockService {

    //  차감 및 상품  수정
    public void decrease(final Product product, final Stock stock, final int quantity) {
        stock.decrease(quantity);
        product.updateQuantity(stock.getQuantity());
    }
}