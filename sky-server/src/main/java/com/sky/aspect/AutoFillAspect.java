package com.sky.aspect;


import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;


@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    @Before("autoFillPointCut()")
    public void AutoFill(JoinPoint joinPoint) {
        MethodSignature ms = (MethodSignature)joinPoint.getSignature();
        AutoFill method = ms.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = method.operationType();
        Object args = joinPoint.getArgs()[0];
        try{
            if(operationType == OperationType.UPDATE){
                    Method setUpdateTime =  args.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                    Method setUpdateUser =  args.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);
                    setUpdateTime.invoke(args,LocalDateTime.now());
                    setUpdateUser.invoke(args,BaseContext.getCurrentId());

                }
            else if(operationType == OperationType.INSERT){
                Method setUpdateTime =  args.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setUpdateUser =  args.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);
                Method setCreateTime =  args.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
                Method setCreateUser =  args.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,Long.class);
                setUpdateTime.invoke(args,LocalDateTime.now());
                setUpdateUser.invoke(args,BaseContext.getCurrentId());
                setCreateTime.invoke(args,LocalDateTime.now());
                setCreateUser.invoke(args,BaseContext.getCurrentId());
                }
        }catch (Exception e) {
            e.printStackTrace();
        }


    }
}
