package com.pinyougou.page.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;

@Component
public class PageDeleteListener implements MessageListener {

    @Autowired
    private ItemPageServiceImpl itemPageServiceImpl;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            Long[] goodsIds = (Long[]) objectMessage.getObject();
            System.out.println("接受到的消息" + goodsIds);
            boolean c = itemPageServiceImpl.deleteItemHtml(goodsIds);
            System.out.println("删除网页" + c);
        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
