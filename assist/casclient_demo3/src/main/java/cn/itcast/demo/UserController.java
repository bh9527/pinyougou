package cn.itcast.demo;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wang heng zuo
 * @date 2018/12/20 20:13
 */

@RestController
public class UserController {

    @RequestMapping("/findLoginUser")
    public void findLoginUser(){
        //获取用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("用户名:"+name);
    }

}
