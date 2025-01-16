package hhplus.newgeniee.ecommerce.user.infrastructure;

import hhplus.newgeniee.ecommerce.user.domain.User;
import hhplus.newgeniee.ecommerce.user.domain.UserRepository;
import lombok.Generated;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    public Optional<User> findById(final long userId) {
        return this.userJpaRepository.findById(userId);
    }

    public boolean existsById(long userId) {
        return this.userJpaRepository.existsById(userId);
    }

    @Generated
    public UserRepositoryImpl(final UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }
}
