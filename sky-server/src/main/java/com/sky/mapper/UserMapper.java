package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    @Select("select * from user where openid=#{openId}")
    User selectByOpenId(String openId);

    @Options(useGeneratedKeys = true,keyProperty = "id")
    @Insert("insert into user(openid, name, phone, sex, id_number, avatar, create_time) " +
            " values (#{openid},#{name},#{phone},#{sex},#{idNumber},#{avatar},#{createTime})")
    void addUser(User user);

    Integer countByMap(@Param("map")Map map);

    Map<String, BigDecimal> countUser(List<Map<String, Object>> dateQueryList);
}
