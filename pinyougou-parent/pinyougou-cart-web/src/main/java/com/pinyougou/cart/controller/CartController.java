package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author wang heng zuo
 * @date 2018/12/21 16:20
 */

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Reference(timeout = 60000)
    private CartService cartService;

    /**
     * 查询购物车明细
     *
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //调用工具类从cookie中获取购物车 字符串
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        //如果cookie中是空,则初始化(获取得到的是字符串)
        if (cartListString == null || cartListString.equals("")) {
            //初始化
            cartListString = "[]";
        }
        //把字符串转换成list集合并返回
        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);

        if (username.equals("anonymousUser")) {//没有登录,从cookie中获取
            System.out.println("cookie取值");

            return cartList_cookie;

        } else {//登录了,就从redis中获取

            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
            if (cartList_cookie.size() > 0) {//如果cookie大于0则进行合并操作
                //调用合并方法,传入redis和cookie存的值
                List<Cart> cartList = cartService.mergeCartList(cartList_redis, cartList_cookie);
                //把合并后的值存入redis
                cartService.saveCartListToRedis(username, cartList);
                //清除cookie中的值
                util.CookieUtil.deleteCookie(request, response, "cartList");
                System.out.println("执行了合并购物车的操作");
                return cartList;
            }


            return cartList_redis;
        }


    }


    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins="http://localhost:9105" )
    public Result addGoodsToCartList(Long itemId, Integer num) {
        //response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");//可以访问的域(当此方法不需要操作cookie)
        //response.setHeader("Access-Control-Allow-Credentials", "true");//如果操作cookie，必须加上这句话

        String username = SecurityContextHolder.getContext().getAuthentication().getName();


        try {
            //1.提取购物车
            List<Cart> cartList = findCartList();

            //2.调用service层操作购物车
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);

            if (username.equals("anonymousUser")) {//没有登录,存到cookie中
                System.out.println("cookid存值");
                //3.将新的购物车存入到cookie中.
                String cartListString = JSON.toJSONString(cartList);
                util.CookieUtil.setCookie(request, response, "cartList", cartListString, 3600 * 24, "UTF-8");

            } else {//如果登录

                cartService.saveCartListToRedis(username, cartList);
            }


            return new Result(true, "购物车添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "购物车添加失败");
        }

    }
}
