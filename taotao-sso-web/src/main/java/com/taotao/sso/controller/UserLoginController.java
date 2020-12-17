package com.taotao.sso.controller;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.CookieUtils;
import com.taotao.sso.service.UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class UserLoginController {
    @Autowired
    private UserLoginService loginService;

    @Value("${TT_TOKEN_KEY}")
    private String TT_TOKEN_KEY;

    /**
     * 登录
     * @param username
     * @return
     */
    @RequestMapping(value = "/user/login",method = RequestMethod.POST)
    @ResponseBody
    public TaotaoResult login(HttpServletRequest request, HttpServletResponse response, String username, String password) {
        //引入服务
        //注入服务
        //调用服务
        TaotaoResult result = loginService.login(username, password);
        //设置token 到cookie cookie 需要跨域
        if (result.getStatus() == 200) {
            CookieUtils.setCookie(request, response, TT_TOKEN_KEY, result.getData().toString());
        }
        return result;
    }

    @RequestMapping(value = "/user/token/{token}", method = RequestMethod.GET)
    @ResponseBody
    public TaotaoResult getUserByToken(@PathVariable String token) {
        //1.调用服务
        TaotaoResult result = loginService.getUserByToken(token);
        return result;
    }
}
