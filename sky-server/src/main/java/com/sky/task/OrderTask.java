package com.sky.task;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    @Scheduled(cron="0 * * * * *")
    public void processTimeoutOrder() {
        log.info("超时订单处理。当前时间：{}", LocalDateTime.now());
        Orders orders = new Orders();
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("订单超时，自动取消");
        orders.setCancelTime(LocalDateTime.now());
        orders.setOrderTime(LocalDateTime.now().plusMinutes(-15));
        orderMapper.changeStatusTimeout(orders,Orders.PENDING_PAYMENT);
    }

    @Scheduled(cron="0 0 1 * * *")
    public void processDeliveryOrder() {
        log.info("派送中订单处理。当前时间：{}", LocalDateTime.now());
        Orders orders = new Orders();
        orders.setStatus(Orders.COMPLETED);
        orderMapper.changeStatus(orders,Orders.DELIVERY_IN_PROGRESS);
    }

}
