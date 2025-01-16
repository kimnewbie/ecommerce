package hhplus.newgeniee.ecommerce.point.infrastructure;

import hhplus.newgeniee.ecommerce.point.domain.Point;
import hhplus.newgeniee.ecommerce.point.domain.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository pointJpaRepository;

    @Override
    public Point save(final Point point) {
        return pointJpaRepository.save(point);
    }

    @Override
    public Optional<Point> findByUserId(final long userId) {
        return pointJpaRepository.findByUserId(userId);
    }

    @Override
    public Optional<Point> findByUserIdForUpdate(final long userId) {
        return pointJpaRepository.findByUserIdForUpdate(userId);
    }
}
