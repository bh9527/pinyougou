package com.pinyougou.sellergoods.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbBrandMapper brandMapper;

    @Autowired
    private TbSellerMapper sellerMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Goods goods) {
        goods.getGoods().setAuditStatus("0");//插入状态
        goodsMapper.insert(goods.getGoods());//插入基本信息

        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());//将商品基本表的ID给商品扩展表
        tbGoodsDescMapper.insert(goods.getGoodsDesc());//插入商品扩展表数据
        //勾选1表示启用,执行语句
        if ("1".equals(goods.getGoods().getIsEnableSpec())) {
            //往tb_item表存数据,把前端得到的值进行存储
            for (TbItem item : goods.getItemList()) {
                //构建标题  SPU名称+ 规格选项值
                String title = goods.getGoods().getGoodsName();//SPU名称
                //把spec里面的json数据转换成map,进行遍历,方便拼接
                Map<String, Object> map = JSON.parseObject(item.getSpec());
                for (String key : map.keySet()) {
                    title += " " + map.get(key);
                }
                //存入表头
                item.setTitle(title);
                setItemValues(item, goods);

                itemMapper.insert(item);
            }
        } else {//没有启用规格

            TbItem item = new TbItem();
            item.setTitle(goods.getGoods().getGoodsName());//标题
            item.setPrice(goods.getGoods().getPrice());//价格
            item.setNum(99999);//库存数量
            item.setStatus("1");//状态
            item.setIsDefault("1");//默认
            item.setSpec("{}");//规格

            setItemValues(item, goods);

            itemMapper.insert(item);
        }


    }

    private void setItemValues(TbItem item, Goods goods) {
        //商品分类
        item.setCategoryid(goods.getGoods().getCategory3Id());//三级分类ID
        item.setCreateTime(new Date());//创建日期
        item.setUpdateTime(new Date());//更新日期

        item.setGoodsId(goods.getGoods().getId());//商品ID
        item.setSellerId(goods.getGoods().getSellerId());//商家ID

        //分类名称
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
        item.setCategory(itemCat.getName());
        //品牌名称
        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        item.setBrand(brand.getName());
        //商家名称(店铺名称)
        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
        item.setSeller(seller.getNickName());

        //图片
        List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if (imageList.size() > 0) {
            item.setImage((String) imageList.get(0).get("url"));
        }

    }

    /**
     * 修改
     */
    @Override
    public void update(Goods goods) {
        //更新基本表数据
        goodsMapper.updateByPrimaryKey(goods.getGoods());
        //更新扩展表数据
        tbGoodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());

        //删除原有的SKU列表数据
        TbItemExample example = new TbItemExample();
        com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getGoods().getId());
        itemMapper.deleteByExample(example);

        //插入新的SKU列表数据
        saveItemList(goods);//插入SKU商品数据
    }


    //插入sku列表数据
    private void saveItemList(Goods goods) {

        if ("1".equals(goods.getGoods().getIsEnableSpec())) {
            for (TbItem item : goods.getItemList()) {
                //构建标题  SPU名称+ 规格选项值
                String title = goods.getGoods().getGoodsName();//SPU名称
                Map<String, Object> map = JSON.parseObject(item.getSpec());
                for (String key : map.keySet()) {
                    title += " " + map.get(key);
                }
                item.setTitle(title);

                setItemValues(item, goods);

                itemMapper.insert(item);
            }
        } else {//没有启用规格

            TbItem item = new TbItem();
            item.setTitle(goods.getGoods().getGoodsName());//标题
            item.setPrice(goods.getGoods().getPrice());//价格
            item.setNum(99999);//库存数量
            item.setStatus("1");//状态
            item.setIsDefault("1");//默认
            item.setSpec("{}");//规格

            setItemValues(item, goods);

            itemMapper.insert(item);
        }

    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {
        Goods goods = new Goods();
        //Goods基本表信息
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setGoods(tbGoods);
        //Desc详情表信息 扩展表
        TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(tbGoodsDesc);
        //item表信息SKU表信息
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        goods.setItemList(tbItems);

        return goods;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setIsDelete("1");//做个标记,逻辑删除
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsDeleteIsNull();//给查询添加IsDeleteIsNull为等于空的才展示的条件
        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                //criteria.andSellerIdLike("%" + goods.getSellerId() + "%");
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }

        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void updateStatus(Long[] ids, String status) {

        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }

    /**
     * 根据SPU的ID集合查询SKU列表
     * @param goodsIds
     * @param status
     * @return
     */
    public List<TbItem> findItemListByGoodsIdListAndStatus(Long[] goodsIds, String status) {

        TbItemExample example=new TbItemExample();
        com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(status);//状态
            //andGoodsIdIn相当于sql中where后面的in(1,2,3)条件.接受值为list,而传入的ids是数组
            //可以使用Arrays.asList转成list
        criteria.andGoodsIdIn( Arrays.asList(goodsIds));//指定条件：SPUID集合

        return itemMapper.selectByExample(example);
    }

}
