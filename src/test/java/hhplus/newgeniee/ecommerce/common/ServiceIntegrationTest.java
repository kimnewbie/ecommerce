package hhplus.newgeniee.ecommerce.common;

import hhplus.newgeniee.ecommerce.coupon.application.CouponService;
import hhplus.newgeniee.ecommerce.coupon.infrastructure.CouponJpaRepository;
import hhplus.newgeniee.ecommerce.coupon.infrastructure.CouponQuantityJpaRepository;
import hhplus.newgeniee.ecommerce.coupon.infrastructure.IssuedCouponJpaRepository;
import hhplus.newgeniee.ecommerce.order.application.OrderService;
import hhplus.newgeniee.ecommerce.order.infrastructure.OrderItemJpaRepository;
import hhplus.newgeniee.ecommerce.order.infrastructure.OrderJpaRepository;
import hhplus.newgeniee.ecommerce.payment.application.PaymentApplicationService;
import hhplus.newgeniee.ecommerce.point.application.PointService;
import hhplus.newgeniee.ecommerce.point.infrastructure.PointJpaRepository;
import hhplus.newgeniee.ecommerce.product.application.ProductService;
import hhplus.newgeniee.ecommerce.product.infrastructure.ProductJpaRepository;
import hhplus.newgeniee.ecommerce.product.infrastructure.StockJpaRepository;
import hhplus.newgeniee.ecommerce.user.infrastructure.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 서비스 통합 테스트를 위한 기본 클래스
 * 필요한 서비스와 Ropositories을 테스트 환경에 자동으로 주입
 */
public abstract class ServiceIntegrationTest extends IntegrationTest {

    @Autowired
    protected CouponService couponService;

    @Autowired
    protected OrderService orderService;

    @Autowired
    protected PaymentApplicationService paymentApplicationService;

    @Autowired
    protected PointService pointService;

    @Autowired
    protected ProductService productService;

    // coupon
    @Autowired
    protected CouponJpaRepository couponJpaRepository;
    @Autowired
    protected IssuedCouponJpaRepository issuedCouponJpaRepository;
    @Autowired
    protected CouponQuantityJpaRepository couponQuantityJpaRepository;

    // order
    @Autowired
    protected OrderJpaRepository orderJpaRepository;
    @Autowired
    protected OrderItemJpaRepository orderItemJpaRepository;

    // product
    @Autowired
    protected ProductJpaRepository productJpaRepository;
    @Autowired
    protected StockJpaRepository stockJpaRepository;

    // point
    @Autowired
    protected PointJpaRepository pointJpaRepository;

    // user
    @Autowired
    protected UserJpaRepository userJpaRepository;

}