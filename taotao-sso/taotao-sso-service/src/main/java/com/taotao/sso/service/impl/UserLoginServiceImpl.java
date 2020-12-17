package com.taotao.sso.service.impl;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.JsonUtils;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.sso.jedis.JedisClient;
import com.taotao.sso.service.UserLoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import redis.clients.jedis.Client;

import java.util.List;
import java.util.UUID;

@Service
public class UserLoginServiceImpl implements UserLoginService {
    @Autowired
    private TbUserMapper userMapper;
    @Autowired
    private JedisClient client;
    @Value("${USER_INFO}")
    private String USER_INFO;
    @Value("${EXPIRE_TIME}")
    private Integer EXPIRE_TIME;

    @Override
    public TaotaoResult login(String username, String password) {
        //注入mapper
        //校验用户名和密码是否为空
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return TaotaoResult.build(400, "用户名和密码错误");
        }

        //先验证用户名
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<TbUser> userList = userMapper.selectByExample(example);
        if (userList != null && userList.size() == 0) {
            return TaotaoResult.build(400, "用户名和密码错误");
        }
        //在验证密码
        TbUser user = userList.get(0);
        String md5DigestAsHex = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!md5DigestAsHex.equals(user.getPassword())) {
            return TaotaoResult.build(400, "用户名和密码错误");
        }
        //如果检验成功
        //生成token :: uuid
        String token = UUID.randomUUID().toString();
        //存放到redis中
        user.setPassword(null);
        client.set(USER_INFO + ":" + token, JsonUtils.objectToJson(user));
        //设置过期时间
        client.expire(USER_INFO + ":" + token, EXPIRE_TIME);
        //把token 设置到cookie
        //cookie跨域

        return TaotaoResult.ok(token);
    }

    @Override
    public TaotaoResult getUserByToken(String token) {
        //1.注入jedisclient
        //2.调用根据token查询用户信息的方法 get 方法
        String strJson = client.get(USER_INFO + ":" + token);
        //3.判断是否查询到
        if (StringUtils.isNotBlank(strJson)) {
            //如果查询到返回200
            TbUser user = JsonUtils.jsonToPojo(strJson, TbUser.class);
            //设置过期时间
            client.expire(USER_INFO + ":" + token, EXPIRE_TIME);
            return TaotaoResult.ok(user);
        }
        //4.如果查不到 返回400
        return TaotaoResult.build(400, "用户已过期");
    }
}
