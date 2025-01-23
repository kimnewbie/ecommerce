package hhplus.newgeniee.ecommerce.global.lock;

import hhplus.newgeniee.ecommerce.global.annotation.RedissonLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedissonLockAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(hhplus.newgeniee.ecommerce.global.annotation.RedissonLock)")
    public Object redissonLock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RedissonLock annotation = method.getAnnotation(RedissonLock.class);

        // SpEL을 사용하여 동적 값 계산
        String lockKey = method.getName() + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), annotation.value());

        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean lockable = lock.tryLock(annotation.waitTime(), annotation.leaseTime(), TimeUnit.MILLISECONDS);
            if (!lockable) {
                log.info("Lock 획득 실패={}", lockKey);
                throw new IllegalStateException("락을 획득할 수 없습니다. 키: " + lockKey);  // 예외 던지기
            }

            log.info("Lock 획득 성공 - 메서드: {}, 파라미터: {}", method.getName(), Arrays.toString(joinPoint.getArgs()));
            Object result = joinPoint.proceed();
            return result;  // 메서드 결과 반환
        } catch (InterruptedException e) {
            log.info("에러 발생");
            throw e;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                log.info("락 해제");
                lock.unlock();
            }
        }
    }

    static class CustomSpringELParser {

        public static Object getDynamicValue(String[] parameterNames, Object[] args, String key) {
            SpelExpressionParser parser = new SpelExpressionParser();
            StandardEvaluationContext context = new StandardEvaluationContext();

            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }

            return parser.parseExpression(key).getValue(context, Object.class);
        }

    }
}
