-- # USER
CREATE TABLE USER
(
    user_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(255) NOT NULL,
    balance    DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
);

-- # CART
CREATE TABLE CART
(
    cart_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    order_qty  INT    NOT NULL,                               -- 주문 수량
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES PRODUCT (product_id), -- 쿠폰 테이블과 외래 키 연결
    FOREIGN KEY (user_id) REFERENCES USER (user_id)
);

-- # PRODUCT
CREATE TABLE PRODUCT
(
    product_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(255)   NOT NULL,
    price           DECIMAL(10, 2) NOT NULL,
    stock_qty       INT       DEFAULT 0, -- 상품 재고 수량
    total_sales_qty INT       DEFAULT 0, -- 총 판매 수량
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- # COUPON
CREATE TABLE COUPON
(
    coupon_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    discount_rate      DECIMAL(5, 2) DEFAULT 0.10,              -- 할인율 (예: 10%)
    status             VARCHAR(20)   DEFAULT 'PENDING',         -- 쿠폰 상태 (예: PENDING, ISSUED, COMPLETED, EXPIRED, CANCELLED 등)
    issued_at          TIMESTAMP     DEFAULT CURRENT_TIMESTAMP, -- 쿠폰 발급 일시
    expiration_at      TIMESTAMP,                               -- 쿠폰 만료 일시
    issued_count       INT           DEFAULT 0,                 -- 발급된 수량
    max_issuable_count INT           DEFAULT 30                 -- 발행 가능 총 수량
);


-- # COUPON_HISTORY
CREATE TABLE COUPON_HISTORY
(
    history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    coupon_id  BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (coupon_id) REFERENCES COUPON (coupon_id), -- 쿠폰 테이블과 외래 키 연결
    FOREIGN KEY (user_id) REFERENCES USER (user_id)
);


-- # ORDER
CREATE TABLE ORDERS
(
    order_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT         NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    status      VARCHAR(20) DEFAULT 'PENDING', -- 주문 상태 (예: COMPLETED, PENDING, CANCELLED)
    created_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES USER (user_id)
);

-- # ORDER_ITEM
CREATE TABLE ORDER_ITEM
(
    order_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id      BIGINT         NOT NULL,
    product_id    BIGINT         NOT NULL,
    order_qty     INT            NOT NULL,      -- 주문 수량
    unit_price    DECIMAL(10, 2) NOT NULL,      -- 상품 단가
    item_status   VARCHAR(20) DEFAULT 'ACTIVE', -- SHIPPED(배송준비), DELIVERED(배송완료), REFUNDED(환불)
    FOREIGN KEY (order_id) REFERENCES ORDERS (order_id),
    FOREIGN KEY (product_id) REFERENCES PRODUCT (product_id)
);

CREATE TABLE USER_POINT
(
    point_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT         NOT NULL,
    amount           INT            NOT NULL,
    transaction_type VARCHAR(20)    NOT NULL,             -- 거래 유형 (예: 'CHARGE', 'USE', 'REFUND')
    balance          DECIMAL(10, 2) NOT NULL,             -- 거래 후 사용자 잔액
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 거래 일시
    FOREIGN KEY (user_id) REFERENCES USER (user_id)       -- USER 테이블과 연결
);


/**
  ERD 명세
// https://dbdiagram.io/d/

Table USER {
  user_id bigint [primary key]
  username varchar
  balance decimal(10, 2) [default: 0.00]
  created_at timestamp [default: 'CURRENT_TIMESTAMP']
}

Table PRODUCT {
  product_id bigint [primary key]
  name varchar
  price decimal(10, 2)
  stock_qty int [default: 0]
  total_sales_qty int [default: 0]
  created_at timestamp [default: 'CURRENT_TIMESTAMP']
}

Table CART {
  cart_id bigint [primary key]
  user_id bigint
  product_id bigint
  order_qty int
  created_at timestamp [default: 'CURRENT_TIMESTAMP']
}

Table COUPON {
  coupon_id bigint [primary key]
  discount_rate decimal(5, 2) [default: 0.10]
  status varchar [default: 'PENDING']
  issued_at timestamp [default: 'CURRENT_TIMESTAMP']
  expiration_at timestamp
  issued_count int [default: 0]
  max_issuable_count int [default: 30]
}

Table COUPON_HISTORY {
  history_id bigint [primary key]
  user_id bigint
  coupon_id bigint
  created_at timestamp [default: 'CURRENT_TIMESTAMP']
}

Table ORDERS {
  order_id bigint [primary key]
  user_id bigint
  total_price decimal(10, 2)
  status varchar [default: 'PENDING']
  created_at timestamp [default: 'CURRENT_TIMESTAMP']
}

Table ORDER_ITEM {
  order_item_id bigint [primary key]
  order_id bigint
  product_id bigint
  order_qty int
  unit_price decimal(10, 2)
  item_status varchar [default: 'ACTIVE']
}

Table USER_POINT {
  point_id bigint [primary key]
  user_id bigint
  amount int
  transaction_type varchar
  balance decimal(10, 2)
  created_at timestamp [default: 'CURRENT_TIMESTAMP']
}

Ref: CART.product_id > PRODUCT.product_id
Ref: CART.user_id > USER.user_id

Ref: COUPON_HISTORY.user_id > USER.user_id
Ref: COUPON_HISTORY.coupon_id > COUPON.coupon_id

Ref: ORDERS.user_id > USER.user_id

Ref: ORDER_ITEM.order_id > ORDERS.order_id
Ref: ORDER_ITEM.product_id > PRODUCT.product_id

Ref: USER_POINT.user_id > USER.user_id


Ref: "USER_POINT"."point_id" < "USER_POINT"."user_id"
 */