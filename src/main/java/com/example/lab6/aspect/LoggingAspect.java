package com.example.lab6.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.example.lab6.controller.*.*(..))")
    public void logBeforeController(JoinPoint joinPoint) {
        logger.info("Entering controller method: {} with arguments: {}",
                joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "execution(* com.example.lab6.controller.*.*(..))", returning = "result")
    public void logAfterController(JoinPoint joinPoint, Object result) {
        logger.info("Exiting controller method: {} with result: {}",
                joinPoint.getSignature().getName(), result);
    }

    @AfterThrowing(pointcut = "execution(* com.example.lab6.controller.*.*(..))", throwing = "exception")
    public void logAfterThrowingController(JoinPoint joinPoint, Throwable exception) {
        logger.error("Exception in controller method: {} with cause: {}",
                joinPoint.getSignature().getName(), exception.getMessage(), exception);
    }

    @Before("execution(* com.example.lab6.service.*.*(..))")
    public void logBeforeService(JoinPoint joinPoint) {
        logger.debug("Entering service method: {} with arguments: {}",
                joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "execution(* com.example.lab6.service.*.*(..))", returning = "result")
    public void logAfterService(JoinPoint joinPoint, Object result) {
        logger.debug("Exiting service method: {} with result: {}",
                joinPoint.getSignature().getName(), result);
    }

    @AfterThrowing(pointcut = "execution(* com.example.lab6.service.*.*(..))", throwing = "exception")
    public void logAfterThrowingService(JoinPoint joinPoint, Throwable exception) {
        logger.error("Exception in service method: {} with cause: {}",
                joinPoint.getSignature().getName(), exception.getMessage(), exception);
    }
}