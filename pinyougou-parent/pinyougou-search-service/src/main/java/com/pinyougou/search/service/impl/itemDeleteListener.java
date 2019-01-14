package com.pinyougou.search.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;
import java.util.Arrays;

@Component
public class itemDeleteListener implements MessageListener {

    @Autowired
    private ItemSearchServiceImpl itemSearchService;
    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage=(ObjectMessage)message;
        try {
            //把object对象强转成long类型
            Long[] goodIds = (Long[]) objectMessage.getObject();
            System.out.println("监听到消息"+goodIds);
                //Arrays.asLis转换成list集合并传入
                itemSearchService.deleteByGoodsIds(Arrays.asList(goodIds));
            System.out.println("索引库删除操作执行啦");

        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
