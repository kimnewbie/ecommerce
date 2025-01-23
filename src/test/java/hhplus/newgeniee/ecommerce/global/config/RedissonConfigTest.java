package hhplus.newgeniee.ecommerce.global.config;

import org.junit.jupiter.api.Test;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedissonConfigTest {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    void redissonClientTest() {
        RMap<String, String> map = redissonClient.getMap("testMap");
        map.put("key", "value");

        String value = map.get("key");
        assertEquals("value", value);
    }
}
