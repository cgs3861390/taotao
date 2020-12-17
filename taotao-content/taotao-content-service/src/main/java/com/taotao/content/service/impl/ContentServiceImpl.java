package com.taotao.content.service.impl;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.JsonUtils;
import com.taotao.content.jedis.JedisClient;
import com.taotao.content.service.ContentService;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private JedisClient jedisClient;

    @Value("${CONTENT_KEY}")
    private String CONTENT_KEY;


    @Autowired
    private TbContentMapper contentMapper;

    @Override
    public TaotaoResult saveContent(TbContent content) {
        //补全属性
        content.setCreated(new Date());
        content.setUpdated(content.getCreated());
        //插入数据
        contentMapper.insert(content);
        //缓存同步
        jedisClient.hdel(CONTENT_KEY, content.getCategoryId().toString());

        return TaotaoResult.ok();

    }

    @Override
    public List<TbContent> getContentListByCatId(Long categoryId) {
        //查询缓存
        try {
            String json = jedisClient.hget(CONTENT_KEY, categoryId + "");
            //判断json是否为空
            if (StringUtils.isNotBlank(json)) {
                //把json转换成list
                List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //根据cid查询内容列表
        TbContentExample example = new TbContentExample();
        //设置查询条件
        example.createCriteria().andCategoryIdEqualTo(categoryId);
        //执行查询
        List<TbContent> list = contentMapper.selectByExample(example);

        //向缓存中添加数据
        try {
            jedisClient.hset(CONTENT_KEY, categoryId + "", JsonUtils.objectToJson(list));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;

    }
}
