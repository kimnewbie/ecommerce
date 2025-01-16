package hhplus.newgeniee.ecommerce.user.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @DisplayName("사용자의 이름으로 User 객체를 생성할 수 있다.")
    @Test
    void createUser() throws Exception {
        // given
        final String name = "뉴진";

        // when
        final User result = User.builder()
                .name(name)
                .build();

        // then
        assertThat(result.getName()).isEqualTo(name);
    }

}