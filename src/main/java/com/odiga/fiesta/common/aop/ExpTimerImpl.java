package com.odiga.fiesta.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;

// https://medium.com/mo-zza/performance-test-4-aop-timer-d40b99fa5b7c
@Aspect
@Component
@Slf4j
public class ExpTimerImpl {

	@Pointcut("@annotation(com.odiga.fiesta.common.aop.ExeTimer)")
	private void timer() {
	}

	@Around("timer()")
	public Object AssumeExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Object result = joinPoint.proceed();
		stopWatch.stop();
		long totalTimeMillis = stopWatch.getTotalTimeMillis();
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		String methodName = signature.getMethod().getName();
		log.info("메서드 이름 : " + methodName);
		log.info("수행 시간 : " + totalTimeMillis + "ms");
		return result;
	}
}
