package com.sky.service;

public interface ShopService {
    void changeShopStatus(Integer status);

    Integer getShopStatus() throws Exception;
}
