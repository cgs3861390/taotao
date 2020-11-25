package com.taotao.service;

import com.taotao.common.pojo.EasyUIDataGridResult;

public interface ItemService {

    public EasyUIDataGridResult getItemList(Integer page, Integer rows);
}
