import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 10, // 동시 사용자 수
    duration: '30s', // 테스트 지속 시간
};

export default function () {
    // 1. GET 요청 테스트
    let res1 = http.get('http://localhost:8080/api/products');
    check(res1, { 'GET /products status is 200': (r) => r.status === 200 });

    // 2. POST 요청 테스트
    let payload = JSON.stringify({ userId: 1, productId: 101, quantity: 2 });
    let params = { headers: { 'Content-Type': 'application/json' } };
    let res2 = http.post('http://localhost:8080/api/orders', payload, params);
    check(res2, { 'POST /orders status is 200': (r) => r.status === 200 });

    // 요청 간격
    sleep(1);
}
