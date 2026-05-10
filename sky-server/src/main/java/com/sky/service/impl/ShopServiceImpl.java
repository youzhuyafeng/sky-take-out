package com.sky.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void changeShopStatus(Integer status)  {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            String s = objectMapper.writeValueAsString(status);
            redisTemplate.opsForValue().set("SHOP_STATUS",s);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Integer getShopStatus() throws Exception {
        try{
            String s = redisTemplate.opsForValue().get("SHOP_STATUS");
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(s,Integer.class);
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception("修改店铺状态失败");
        }
    }
}
