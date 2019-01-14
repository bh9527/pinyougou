package com.itheima.demo;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author wang heng zuo
 * @date 2018/12/19 18:12
 */

@Component
public class C {

    @JmsListener(destination = "itcast")
    public void readMessage(String text) {
        System.out.println("接收到消息：" + text);

    }

    @JmsListener(destination = "itcast_map")
    public void readMap(Map map) {
        System.out.println(map);
    }

}
