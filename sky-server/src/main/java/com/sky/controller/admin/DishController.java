package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @PostMapping
    public Result<String> saveDish(@RequestBody DishDTO dish){
        log.info("保存菜品：{}",dish);
        dishService.saveDishWithFlavor(dish);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> getDishList(DishPageQueryDTO dishPageQueryDTO){
        log.info("获取菜品列表");
        PageResult pageResult =  dishService.getDishList(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    public Result<String> deleteDish(@RequestParam List<Long> ids){
        log.info("删除菜品：{}",ids);
        dishService.delete(ids);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    public Result<String> setDishStatus(@PathVariable Integer status,@RequestParam Long id){
        dishService.setDishStatus(status,id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<DishVO> getDishById(@PathVariable Long id){
        DishVO dish = dishService.getDishById(id);
        return Result.success(dish);
    }
}
