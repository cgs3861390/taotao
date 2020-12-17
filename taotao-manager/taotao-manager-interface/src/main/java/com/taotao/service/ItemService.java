package com.taotao.service;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;

public interface ItemService {

    public EasyUIDataGridResult getItemList(Integer page, Integer rows);

    public TaotaoResult saveItem(TbItem item, String desc);

    public TbItem getItemById(Long itemId);
    public TbItemDesc getItemDescById(Long itemId);
}
