package com.pinyougou.page.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;


@Component
public class pageListener implements MessageListener {

    @Autowired
    private ItemPageServiceImpl itemPageService;
    @Override
    public void onMessage(Message message) {
        //获取参数转换成text类型
        TextMessage textMessage=(TextMessage)message;
        //获取文本
        try {
            String text = textMessage.getText();
            //把字符串转换成Long类型
            boolean b = itemPageService.genItemHtml(Long.parseLong(text));
            System.out.println("网页生成结果"+b);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
