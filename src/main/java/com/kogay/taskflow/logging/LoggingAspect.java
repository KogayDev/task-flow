package com.kogay.taskflow.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {
    }

    @Pointcut("execution( * com.kogay..*.*(..)) ")
    public void applicationPackagePointcut() {
    }

    @Around("springBeanPointcut() && applicationPackagePointcut()")
    public Object logMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("Enter: {}.{}() with argument[s] = {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
        Object result;
        try {
            result = joinPoint.proceed();
            log.debug("Exit: {}.{}() with result = {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), result);
            return result;
        } catch (Exception e) {
            log.debug("Exception: {} with message: {}, in method: {}.{}()",
                    e.getClass(),
                    e.getLocalizedMessage(),
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName());
            throw e;
        }
    }
}
