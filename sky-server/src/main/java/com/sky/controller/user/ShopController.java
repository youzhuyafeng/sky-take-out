package com.sky.controller.user;


import com.sky.result.Result;
import com.sky.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("userShopController")
@RequestMapping("/user/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @GetMapping("/status")
    public Result<Integer> GetShopStatus() throws Exception {
        log.info("查询店铺状态");
        Integer status = shopService.getShopStatus();
        return Result.success(status);
    }
}
