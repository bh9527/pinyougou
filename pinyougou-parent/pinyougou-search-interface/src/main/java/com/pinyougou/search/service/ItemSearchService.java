package com.pinyougou.search.service;

import com.pinyougou.pojo.TbSeckillOrder;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    /**
     * 搜索方法
     * @param searchMap 接受map集合
     * @return返回map集合
     */

    public Map search(Map searchMap);


    /**
     * 导入列表
     * @param list
     */
    public void importList(List list);

    /**
     * 删除商品列表
     * @param goodsIds  (SPU)
     */
    public void deleteByGoodsIds(List goodsIds);


}
