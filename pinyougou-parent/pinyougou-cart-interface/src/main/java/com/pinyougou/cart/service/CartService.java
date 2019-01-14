package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

/**
 * @author wang heng zuo
 * @date 2018/12/21 14:19
 *
 */

//购物车服务接口
public interface CartService {

    /**
     * 添加商品到购物车列表
     * @param cartList
     * @param itemId SKU item表的ID
     * @param num  数量
     * @return
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);


    /**
     * 把购物车信息从redis中取出
     * @param username 用户名
     * @return
     */
    public List<Cart> findCartListFromRedis(String username);

    /**
     * 将购物车列表存入redis
     * @param username
     * @param cartList
     */
    public void  saveCartListToRedis(String username,List<Cart> cartList);

    /**
     * 合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    public List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);
}
