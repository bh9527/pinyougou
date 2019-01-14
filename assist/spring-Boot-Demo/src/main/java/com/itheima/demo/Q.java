package com.itheima.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wang heng zuo
 * @date 2018/12/19 18:06
 */

@RestController
public class Q {

    @Resource
    private JmsMessagingTemplate jmsMessagingTemplate;

    @RequestMapping("/send")
    public void send(String text) {
        jmsMessagingTemplate.convertAndSend("itcast", text);
    }


    @RequestMapping("/sendmap1")
    public void sendMap1(){
        Map map=new HashMap<>();
        map.put("mobile", "15717135316");
        map.put("content", "恭喜获得10元代金券");
        jmsMessagingTemplate.convertAndSend("itcast_map",map);
    }

    @RequestMapping("/sendmap")
    public void sendMap(){
        Map map=new HashMap<>();
        map.put("mobile", "15717135316");
        map.put("template_code", "SMS_150578711");
        map.put("sign_name", "品优购电商项目后台");
        map.put("param", "{\"name\":\"lml520\"}");
        jmsMessagingTemplate.convertAndSend("sms",map);
        System.out.println("aaa");
    }


}
