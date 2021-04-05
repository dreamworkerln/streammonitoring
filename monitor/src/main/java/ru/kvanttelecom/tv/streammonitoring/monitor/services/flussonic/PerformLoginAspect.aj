package ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Aspect
@Component
public aspect PerformLoginAspect {

//    @Before("@annotation(PerformWatcherLogin)")
//    public void validateAuthenticationType(JoinPoint joinPoint) {
//        //MethodSignature signature = (MethodSignature)joinPoint.getSignature();
//
//        WatcherGrabber wg = (WatcherGrabber)joinPoint.getTarget();
//        wg.login();
//    }
}
