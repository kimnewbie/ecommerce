package hhplus.newgeniee.ecommerce.util;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * JPA 엔티티 객체에 테스트를 위한 id 직접 설정
 */
public class EntityIdSetter {

    public static void setId(Object entity, Long id) {
        ReflectionTestUtils.setField(entity, "id", id);
    }
}