package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("adminShopController")
@RequestMapping("/admin/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @PutMapping("/{status}")
    public Result<String> ChangeShopStatus(@PathVariable Integer status){
        log.info("修改店铺营业状态为{}",status==1?"营业":"打烊");
        shopService.changeShopStatus(status);
        return Result.success();
    }

    @GetMapping("/status")
    public Result<Integer> GetShopStatus() throws Exception {
        log.info("查询店铺状态");
        Integer status = shopService.getShopStatus();
        return Result.success(status);
    }
}
