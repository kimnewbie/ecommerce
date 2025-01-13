package hhplus.newgeniee.ecommerce.point.infrastructure;

import hhplus.newgeniee.ecommerce.point.domain.Point;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointJpaRepository extends JpaRepository<Point, Long> {
}
