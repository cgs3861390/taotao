package com.taotao.sso.service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;

/**
 * 用户注册接口
 */
public interface UserRegisterService {
    /**
     * 根据参数和类型进行校验
     * @param param 校验的数据
     * @param type 校验的数据类型 1 2 3
     * @return
     */
    public TaotaoResult checkData(String param, Integer type);

    /**
     * 验证注册是否可用
     * @param tbUser
     * @return
     */
    public TaotaoResult register(TbUser tbUser);
}
