package com.sky.controller.user;


import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("list")
    public Result<List<ShoppingCart>> list(){
        return Result.success(shoppingCartService.list());
    }

    @PostMapping("add")
    public Result<String > add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.addMealOrDish(shoppingCartDTO);
        return Result.success();
    }

    @PostMapping("/sub")
    public Result<String> sub(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("删除购物车中一个商品，商品：{}", shoppingCartDTO);
        shoppingCartService.subShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    @DeleteMapping("/clean")
    public Result<String> clean(){
        log.info("清空购物车");
        shoppingCartService.clean();
        return Result.success();
    }
}
