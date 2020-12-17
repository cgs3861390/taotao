package com.taotao.search.mapper;

import com.taotao.common.pojo.SearchItem;

import java.util.List;

public interface SearchItemMapper {
    //查询所有商品的数据
    public List<SearchItem> getSearchItemList();

    //根据商品id查询商品信息
    public SearchItem getItemById(Long itemId);
}
