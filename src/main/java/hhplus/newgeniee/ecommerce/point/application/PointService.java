package hhplus.newgeniee.ecommerce.point.application;

import hhplus.newgeniee.ecommerce.point.api.request.PointReloadRequest;
import hhplus.newgeniee.ecommerce.point.api.response.PointReloadResponse;
import hhplus.newgeniee.ecommerce.point.api.response.PointResponse;
import hhplus.newgeniee.ecommerce.point.domain.Point;
import hhplus.newgeniee.ecommerce.point.domain.PointRepository;
import hhplus.newgeniee.ecommerce.user.domain.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 관련 비즈니스 로직 서비스
 */

@RequiredArgsConstructor
@Service
public class PointService {

    private final PointRepository pointRepository;
    private final UserRepository userRepository;

    @Cacheable(cacheNames = "points", key = "#userId")
    public PointResponse getPoint(final Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("사용자를 찾을 수 없습니다.");
        }

        final Point point = pointRepository.findByUserId(userId)
                .orElseGet(() -> pointRepository.save(Point.empty(userId)));
        return PointResponse.from(point);
    }

    @Transactional
    public PointReloadResponse reload(final PointReloadRequest request) {
        if (!userRepository.existsById(request.getUserId())) {
            throw new EntityNotFoundException("사용자를 찾을 수 없습니다.");
        }

        final Point point = pointRepository.findByUserIdForUpdate(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("포인트 정보를 찾을 수 없습니다."));

        point.reload(request.getReloadPoint());
        return PointReloadResponse.from(point);
    }
}