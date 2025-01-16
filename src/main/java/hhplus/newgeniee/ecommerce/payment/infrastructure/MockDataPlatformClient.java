package hhplus.newgeniee.ecommerce.payment.infrastructure;

import hhplus.newgeniee.ecommerce.payment.domain.DataPlatformClient;
import hhplus.newgeniee.ecommerce.payment.domain.OrderData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MockDataPlatformClient implements DataPlatformClient {

    @Async
    @Override
    public void sendOrderData(final OrderData orderData) {
        try {
            log.info("send order data. {}", orderData);
        } catch (Exception e) {
            log.error("Failed to send OrderData. orderId={}", orderData.getOrderId(), e);
        }
    }
}
