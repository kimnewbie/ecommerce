package hhplus.newgeniee.ecommerce.user.infrastructure;

import hhplus.newgeniee.ecommerce.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
}