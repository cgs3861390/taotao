package com.taotao.content.service;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import java.util.List;

public interface ContentCategoryService {
    //通过节点的ID 查询该节点的子节点列表
    public List<EasyUITreeNode> getContentCategoryList(Long parentId);
    //添加内容分类

    /**
     *
     * @param parentId 父节点ID
     * @param name 新增节点名称
     * @return
     */
    public TaotaoResult createContentCategory(Long parentId, String name);

}
