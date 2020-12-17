package com.taotao.sso.service;

import com.taotao.common.pojo.TaotaoResult;

/**
 * 登录的接口
 */
public interface UserLoginService {
    /**
     * 根据用户名和密码登录
     * @param username
     * @param password
     * @return
     */
    public TaotaoResult login(String username, String password);
    public TaotaoResult getUserByToken(String token);
}
