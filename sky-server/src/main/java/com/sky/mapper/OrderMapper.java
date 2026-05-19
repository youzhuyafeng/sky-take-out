package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    void insert(Orders order);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);

    void update(Orders orders);

    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer toBeConfirmed);

    //TODO 可以合并
    @Update("update orders set status=#{orders.status},cancel_reason=#{orders.cancelReason},cancel_time=#{orders.cancelTime} " +
            "where order_time < #{orders.orderTime} and status=#{orderStatus}")
    void changeStatusTimeout(@Param("orders")Orders orders,@Param("orderStatus")Integer orderStatus);

    @Update("update orders set status=#{orders.status} where status=#{orderStatus}")
    void changeStatus(@Param("orders")Orders orders, @Param("orderStatus")Integer orderStatus);

    Map<String, BigDecimal> addAmountByDate(List<Map<String, Object>> dateQueryList);
}
