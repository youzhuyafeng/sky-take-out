package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    public static final String WX_LOGIN="https://api.weixin.qq.com/sns/jscode2session";

    private final WeChatProperties weChatProperties;

    private final UserMapper userMapper;

    private final JwtProperties jwtProperties;

    @Autowired
    public UserServiceImpl(WeChatProperties weChatProperties, UserMapper userMapper, JwtProperties jwtProperties) {
        this.weChatProperties = weChatProperties;
        this.userMapper = userMapper;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public UserLoginVO login(UserLoginDTO userLoginDTO){
        Map<String,String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("grant_type","authorization_code");
        map.put("js_code",userLoginDTO.getCode());

        String response = HttpClientUtil.doGet(WX_LOGIN,map);
        ObjectMapper objectMapper = new ObjectMapper();
        String openId = null;
        try{
            JsonNode node =  objectMapper.readTree(response);
            openId = node.get("openid").asText();
        }catch (Exception e){
            e.printStackTrace();
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        User user= userMapper.selectByOpenId(openId);
        if(user==null){
            user=new User();
            user.setOpenid(openId);
            user.setCreateTime(LocalDateTime.now());
            userMapper.addUser(user);
        }
        Map<String,Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(),jwtProperties.getUserTtl(),claims);
        return new UserLoginVO(user.getId(),openId,token);

    }
}
