package hhplus.newgeniee.ecommerce.infra.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/point/{userId}")
@Tag(name = "User Point API", description = "사용자 포인트 관련 API")
public class UserPointController {

    private final Map<Long, BigDecimal> userPoints = new HashMap<>() {{
        put(1L, new BigDecimal("1000.50"));
        put(2L, new BigDecimal("500.75"));
        put(3L, new BigDecimal("1500.25"));
    }};

    @Operation(
            summary = "사용자의 포인트 조회",
            description = "사용자의 포인트를 조회",
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "사용자의 ID",
                            required = true,
                            example = "1"
                    )
            }
    )
    @GetMapping
    public BigDecimal getUserPoints(@PathVariable Long userId) {
        return userPoints.getOrDefault(userId, BigDecimal.ZERO);
    }

    @Operation(
            summary = "사용자 포인트 충전",
            description = "사용자 포인트 충전",
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "사용자의 ID",
                            required = true,
                            example = "1"
                    ),
                    @Parameter(
                            name = "amount",
                            description = "충전할 포인트 금액",
                            required = true,
                            example = "500.00"
                    )
            }
    )
    @PostMapping("/charge")
    public BigDecimal chargeUserPoints(@PathVariable Long userId, BigDecimal amount) {
        userPoints.put(userId, userPoints.getOrDefault(userId, BigDecimal.ZERO).add(amount));
        return userPoints.get(userId);
    }
}
