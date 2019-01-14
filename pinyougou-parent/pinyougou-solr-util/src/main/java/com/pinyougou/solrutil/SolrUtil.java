package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil  {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

//创建导入数据库到solr中
    public void importItemData(){
        //查询所有item表中的数据

        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        //增加有效数据
        criteria.andStatusEqualTo("1");
        //根据条件查询结果
        List<TbItem> itemList = itemMapper.selectByExample(example);

        for (TbItem item : itemList) {
            //item表中的spec是一个json字符串,需要进行转换成对象
            Map map = JSON.parseObject(item.getSpec(), Map.class);
            //把对象存入集合
            item.setSpecMap(map);


        }

        //调用solr工具吧结果集存入到缓存 记得一定要带saveBeans

        solrTemplate.saveBeans(itemList);
        //提交
        solrTemplate.commit();


    }


    /**
     * 删除索引
     */
    public void deleteAll(){
        Query query =new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }


    public static void main(String[] args) {
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = (SolrUtil) applicationContext.getBean("solrUtil");

       /* solrUtil.importItemData();*/
        //删除索引方法
        solrUtil.deleteAll();
    }

}
