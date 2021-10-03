package com.timely.projectservice.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class ExecutionTimeTracker {

    @Around("@annotation(com.timely.projectservice.util.TrackExecutionTime)")
    public Object trackExecutionTime(ProceedingJoinPoint point) throws Throwable {
        Long startTime = System.currentTimeMillis();
        log.info("started executing {}", point.getSignature());
        Object result = point.proceed();
        Long endTime = System.currentTimeMillis();
        log.info("finished executing {}, took {}", point.getSignature(), endTime - startTime);
        return result;
    }

}
