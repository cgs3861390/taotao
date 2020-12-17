package com.taotao.sso.service.impl;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.sso.service.UserRegisterService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

@Service
public class UserRegisterServiceImpl implements UserRegisterService {

    @Autowired
    private TbUserMapper userMapper;

    @Override
    public TaotaoResult checkData(String param, Integer type) {
        //注入mappper
        //根据参数动态生成查询条件
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        if (type == 1) {//username
            if (StringUtils.isEmpty(param)) {
                return TaotaoResult.ok(false);
            }
            criteria.andUsernameEqualTo(param);

        }else if (type == 2) {
            //phone
            criteria.andPhoneEqualTo(param);
        }else if (type == 3) {
            //email
            criteria.andEmailEqualTo(param);
        }else {
            //非法参数
            return TaotaoResult.build(400, "非法的参数");
        }
        //调用mapper的查询方法获取数据
        List<TbUser> tbUsers = userMapper.selectByExample(example);
        //如果查到了数据 数据不可用
        if (tbUsers != null && tbUsers.size() > 0) {
            return TaotaoResult.ok(false);
        }
        //如果没有查询到数据 数据可以使用
        return TaotaoResult.ok(true);

    }

    @Override
    public TaotaoResult register(TbUser tbUser) {
        //注入mapper
        //校验数据
        //2.1 校验用户名和密码不能为空
        if (StringUtils.isEmpty(tbUser.getUsername())) {
            return TaotaoResult.build(400, "注册失败，请校验数据后再提交");
        }
        if (StringUtils.isEmpty(tbUser.getPassword())) {
            return TaotaoResult.build(400, "注册失败，请校验数据后再提交数据");
        }
        //2.2 校验用户名是否被注册了
        TaotaoResult result = checkData(tbUser.getUsername(), 1);
        if (!(boolean)result.getData()) {
            //数据不可用
            return TaotaoResult.build(400, "注册失败，请校验数据后再提交数据");
        }
        //2.3 校验电话是否被注册了
        if (StringUtils.isNotBlank(tbUser.getPhone())) {
            TaotaoResult result2 = checkData(tbUser.getPhone(), 2);
            if (!(boolean)result.getData()) {
                return TaotaoResult.build(400, "注册失败，请校验数据后再提交数据");
            }
        }
        //2.4 校验email是否被校验了
        if (StringUtils.isNotBlank(tbUser.getEmail())) {
            TaotaoResult result2 = checkData(tbUser.getEmail(), 3);
            if (!(boolean)result.getData()) {
                return TaotaoResult.build(400, "注册失败，请校验数据后再提交数据");
            }
        }
        //如果检验成功，补全其他数据
        tbUser.setCreated(new Date());
        tbUser.setUpdated(tbUser.getCreated());
        //密码加密
        String md5Password = DigestUtils.md5DigestAsHex(tbUser.getPassword().getBytes());
        tbUser.setPassword(md5Password);
        //插入数据
        userMapper.insertSelective(tbUser);
        //返回结果
        return TaotaoResult.ok();
    }
}
