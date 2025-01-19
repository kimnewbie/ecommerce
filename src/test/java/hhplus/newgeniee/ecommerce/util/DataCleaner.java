package hhplus.newgeniee.ecommerce.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.Type;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * JPA 엔티티와 매핑된 모든 테이블을
 * truncate 작업을 수행하는 기능
 */
@Component
public class DataCleaner {

    private final EntityManager em;
    private final List<String> tableNames;

    public DataCleaner(final EntityManager em) {
        this.em = em;
        this.tableNames = em.getMetamodel()
                .getEntities() // 모든 Entity 가져오기
                .stream()
                .map(Type::getJavaType) // 각 엔티티의 Java 타입을 가져오기
                .map(javaType -> javaType.getAnnotation(Table.class)) // @Table 어노테이션 가져오기
                .map(Table::name) // 테이블의 이름 추출
                .toList();
    }

    @Transactional
    public void clean() {
        em.flush(); // 영속성 컨텍스트에 있는 모든 변경사항을 db에 반영
        tableNames.forEach(this::truncateTable); // 테이블 비우기
    }

    private int truncateTable(final String tableName) {
        return em.createNativeQuery("truncate table " + tableName)
                .executeUpdate();
    }
}
