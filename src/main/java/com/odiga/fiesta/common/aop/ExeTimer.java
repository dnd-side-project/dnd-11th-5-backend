package com.odiga.fiesta.common.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// https://medium.com/mo-zza/performance-test-4-aop-timer-d40b99fa5b7c
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExeTimer {
}
