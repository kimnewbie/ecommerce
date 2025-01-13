package hhplus.newgeniee.ecommerce.point.application;

import hhplus.newgeniee.ecommerce.point.domain.PointRepository;

/**
 * 관련 비즈니스 로직 서비스
 */
public class PointApplicationService {
    private final PointRepository pointRepository;

    public PointApplicationService(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

}
