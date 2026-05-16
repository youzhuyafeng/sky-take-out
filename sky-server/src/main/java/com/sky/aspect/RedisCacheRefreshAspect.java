package com.sky.aspect;
import com.sky.annotation.RedisCacheRefresh;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@Aspect
public class RedisCacheRefreshAspect {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Pointcut("execution(* com.sky.service.*.*(..)) && @annotation(com.sky.annotation.RedisCacheRefresh)")
    public void RedisCacheRefreshPointcut(){}

    @After("RedisCacheRefreshPointcut()")
    public void RedisCacheRefresh(JoinPoint joinPoint){
        MethodSignature method= (MethodSignature)joinPoint.getSignature();
        RedisCacheRefresh annotation =  method.getMethod().getAnnotation(RedisCacheRefresh.class);
        Object arg = joinPoint.getArgs()[0];
        if(annotation.argsClass() == List.class){
            if(arg instanceof List<?>){
                ((List<?>) arg).forEach(item->{
                    stringRedisTemplate.delete("dish_"+item.toString());
                    log.info("批量清除redis dish_{}缓存",item.toString());
                });
            }
        }else if(annotation.argsClass() == DishDTO.class){
            if(arg instanceof DishDTO){
                DishDTO dish = (DishDTO)arg;
                if(dish.getCategoryId() != null){
                    stringRedisTemplate.delete("dish_"+dish.getCategoryId());
                    log.info("清除redis dish_{}缓存",dish.getCategoryId());
                }
            }

        }else{
            log.info("清除redis dish缓存");
            Set keys = stringRedisTemplate.keys("dish_*");
            stringRedisTemplate.delete(keys);
        }

    }

}
