package com.pinyougou.page.service.impl;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {
    //获取网页静态化工厂
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    //把配置文件注入(为html页面路径)
    @Value("${pagedir}")
    private String pagedir;

    //商品基础数据 spu
    @Autowired
    private TbGoodsMapper goodsMapper;

    //商品扩展数据
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    //商品详情表 sku
    @Autowired
    private TbItemCatMapper itemCatMapper;

    //商品类目表
    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public boolean genItemHtml(Long goodsId) {

        //获取配置对象
        Configuration configuration = freeMarkerConfigurer.getConfiguration();

        try {
            //获取模板
            Template template = configuration.getTemplate("item.ftl");
            //创建数据模型
            Map dataModel = new HashMap();
            //1.获取主表信息
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goods", goods);

            //2.获取扩展表信息
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goodsDesc", goodsDesc);

            //3.读取商品分类

            String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
            dataModel.put("itemCat1", itemCat1);
            dataModel.put("itemCat2", itemCat2);
            dataModel.put("itemCat3", itemCat3);

            //4.读取SKU列表

            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);//对应SPU的
            criteria.andStatusEqualTo("1");//激活的
            example.setOrderByClause("is_default desc");//按是否默认字段进行降序排序，目的是返回的结果第一条为默认SKU
            List<TbItem> itemList = itemMapper.selectByExample(example);

            //封装到detaModel中
            dataModel.put("itemList", itemList);


            //创建输出流(网页地址指定路径名+id)
            Writer out = new FileWriter(pagedir + goodsId + ".html");

            //输出
            template.process(dataModel, out);

            //关闭流
            out.close();
            return true;


        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }


    @Override
    public boolean deleteItemHtml(Long[] goodsIds) {
        try {
            for (Long goodsId : goodsIds) {
                //删除详情页
                new File(pagedir + goodsId + ".html").delete();

            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }
}
