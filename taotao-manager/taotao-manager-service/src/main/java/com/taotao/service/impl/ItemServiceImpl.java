package com.taotao.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.IDUtils;
import com.taotao.common.util.JsonUtils;
import com.taotao.manager.jedis.JedisClient;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemExample;
import com.taotao.service.ItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.jms.core.JmsTemplate;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.Date;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbItemDescMapper itemDescMapper;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Resource
    private Destination topicDestination;

    @Autowired
    private JedisClient jedisClient;

    @Value("${TIME_INFO_KEY}")
    private String TIME_INFO_KEY;

    @Value("${TIME_INFO_KEY_EXPIRE}")
    private Integer TIME_INFO_KEY_EXPIRE;

    @Override
    public EasyUIDataGridResult getItemList(Integer page, Integer rows) {
        if (page == null)
            page = 1;
        if (rows == null)
            rows = 30;
        //设置分页信息
        PageHelper.startPage(page, rows);
        //执行查询
        TbItemExample example = new TbItemExample();
        List<TbItem> list = itemMapper.selectByExample(example);
        //取分页信息
        PageInfo<TbItem> pageInfo = new PageInfo<>(list);

        //创建返回结果对象
        EasyUIDataGridResult result = new EasyUIDataGridResult();
        result.setTotal((int) pageInfo.getTotal());
        result.setRows(pageInfo.getList());

        return result;

    }



    @Override
    public TaotaoResult saveItem(TbItem item, String desc) {
        // 1、生成商品id
        long itemId = IDUtils.genItemId();
        // 2、补全TbItem对象的属性
        item.setId(itemId);
        item.setCreated(new Date());
        item.setStatus((byte)1);
        item.setUpdated(item.getCreated());
        // 3、向商品表插入数据
        itemMapper.insertSelective(item);
        // 4、创建一个TbItemDesc对象
        TbItemDesc desc2 = new TbItemDesc();
        // 5、补全TbItemDesc的属性
        desc2.setItemDesc(desc);
        desc2.setItemId(itemId);
        desc2.setCreated(item.getCreated());
        desc2.setUpdated(item.getCreated());
        // 6、向商品描述表插入数据
        itemDescMapper.insertSelective(desc2);
        //发送一个商品添加消息
        jmsTemplate.send(topicDestination, new MessageCreator() {

            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage(itemId + "");
                return textMessage;
            }
        });

        //返回TaotaoResult
        return TaotaoResult.ok();

    }

    @Override
    public TbItem getItemById(Long itemId) {
        //添加缓存
        //1.从缓存中获取数据
        try {
            String jsonStr = jedisClient.get(TIME_INFO_KEY + ":" + itemId + ":BASE");
            if(StringUtils.isNotBlank(jsonStr)) {
                //设置有效期
                jedisClient.expire(TIME_INFO_KEY + ":" + itemId + ":BASE", TIME_INFO_KEY_EXPIRE);
                return JsonUtils.jsonToPojo(jsonStr, TbItem.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //2.没数据
        //注入mapper
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        //调用方法
        //返回tbitem
        //3.添加缓存到redis数据库中
        try {
            //注入jedisclient
            jedisClient.set(TIME_INFO_KEY + ":" + itemId + ":BASE", JsonUtils.objectToJson(tbItem));
            //设置有效期
            jedisClient.expire(TIME_INFO_KEY + ":" + itemId + ":BASE", TIME_INFO_KEY_EXPIRE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tbItem;
    }

    @Override
    public TbItemDesc getItemDescById(Long itemId) {
        //添加缓存
        //1.从缓存中获取数据
        try {
            String jsonStr = jedisClient.get(TIME_INFO_KEY + ":" + itemId + ":BASE");
            if(StringUtils.isNotBlank(jsonStr)) {
                //设置有效期
                jedisClient.expire(TIME_INFO_KEY + ":" + itemId + ":BASE", TIME_INFO_KEY_EXPIRE);
                return JsonUtils.jsonToPojo(jsonStr, TbItemDesc.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        TbItemDesc tbItemDesc = itemDescMapper.selectByPrimaryKey(itemId);
        //3.添加缓存到redis数据库中
        try {
            //注入jedisclient
            jedisClient.set(TIME_INFO_KEY + ":" + itemId + ":BASE", JsonUtils.objectToJson(tbItemDesc));
            //设置有效期
            jedisClient.expire(TIME_INFO_KEY + ":" + itemId + ":BASE", TIME_INFO_KEY_EXPIRE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tbItemDesc;
    }
}
