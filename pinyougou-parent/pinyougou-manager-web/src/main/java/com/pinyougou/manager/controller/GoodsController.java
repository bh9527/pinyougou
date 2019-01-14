package com.pinyougou.manager.controller;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return goodsService.findPage(page, rows);
    }


    /**
     * 修改
     *
     * @param goods
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            goodsService.update(goods);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findOne(id);
    }

    @Autowired
    private Destination queueSolrDeleteDestination; //导入solr索引的消息目标(点对点)

    @Autowired
    private Destination topicPageDestination; //生成详情页的消息目标(发布订阅)

    @Autowired
    private Destination topicPageDeleteDestination; //删除详情页的消息目标
    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(final Long[] ids) {
        try {
            goodsService.delete(ids);
            //从索引中删除
            // itemSearchService.deleteByGoodsIds(Arrays.asList(ids));

            jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                            //直接把ids使用对象消息者传递过去(Long就是一个可序列化的对象)
                    return session.createObjectMessage(ids);
                }
            });


        //删除每个服务器上的商品详情页
            jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {


                    return session.createObjectMessage(ids);
                }
            });


            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbGoods goods, int page, int rows) {
        return goodsService.findPage(goods, page, rows);
    }

    //调用其他层,追加时间,以免超时
    // @Reference(timeout = 100000)
    // private ItemSearchService itemSearchService;
    //操作中间件的消息发送和接受
    @Autowired
    private JmsTemplate jmsTemplate;
    //消息目的地
    @Autowired
    private Destination queueSolrDestination;

    //修改状态
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status) {
        try {
            goodsService.updateStatus(ids, status);
            //如果审核通过了
            if ("1".equals(status)) {
                //调用查询需要更新的SKU列表方法
                List<TbItem> list = goodsService.findItemListByGoodsIdListAndStatus(ids, status);
                //调用更新索引库方法
                //    itemSearchService.importList(list);
                //把list集合转成字符串,进行中间件的消息传送,需final修饰
                final String jsonString = JSON.toJSONString(list);
                //参数1:目的地 参数2:发送的内容
                jmsTemplate.send(queueSolrDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        //把消息传入,消息必须是final修饰的
                        return session.createTextMessage(jsonString);
                    }
                });


                //生成商品详情html页面
                for (final Long goodsId : ids) {

                    //itemPageService.genItemHtml(goodsId);
                jmsTemplate.send(topicPageDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {

                        return session.createTextMessage(goodsId+"");
                    }
                });

                }

            }

            return new Result(true, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "失败");
        }
    }


    //@Reference(timeout = 50000)
   // private ItemPageService itemPageService;

    @RequestMapping("/genHtml")
    public void genHtml(Long goodsId) {

       // itemPageService.genItemHtml(goodsId);
    }

}
