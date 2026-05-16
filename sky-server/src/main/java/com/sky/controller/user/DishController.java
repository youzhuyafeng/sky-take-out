package com.sky.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
public class DishController {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DishService dishService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public Result<List<DishVO>> list(Long categoryId) {
        String query = "dish_" + categoryId;
        String dishList = stringRedisTemplate.opsForValue().get(query);
        if (dishList != null) {
            List<DishVO> cache = null;
            try {
                //TODO 序列化类中有LocalDateTime字段，目前没有特殊处理。
                //如果将来发生反序列化异常需要在类或配置文件中规定序列化格式。
                cache = objectMapper.readValue(dishList, new TypeReference<List<DishVO>>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            if(cache != null && !cache.isEmpty()) {
                return Result.success(cache);
            }
        }
        log.info("id为{}的菜品不在缓存中，正在向数据库查询", categoryId);
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

        List<DishVO> list = dishService.listWithFlavor(dish);
        try {
            dishList  = objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        stringRedisTemplate.opsForValue().set("dish_"+categoryId,dishList);

        return Result.success(list);
    }

}
