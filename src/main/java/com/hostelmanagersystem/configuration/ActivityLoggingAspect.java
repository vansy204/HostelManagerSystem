package com.hostelmanagersystem.configuration;

import com.hostelmanagersystem.annotation.LogActivity;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect // dinh  nghia aop aspect
@Component
@Slf4j
public class ActivityLoggingAspect {
    @Around("@annotation(logActivity)") // around de chay truoc va sau method duoc annotate
    public Object logActivity(ProceedingJoinPoint joinPoint, LogActivity logActivity) throws Throwable {
        long startTime = System.currentTimeMillis();
        try{
            Object result = joinPoint.proceed(); // chay method goc
            // log thanh cong
            return result;
        }catch (Exception e){
            //log loi
            throw e;
        }
    }
}
