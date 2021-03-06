package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wang heng zuo
 * @date 2018/12/21 14:57
 */

@Service(timeout = 50000)

public class CartServiceImpl implements CartService {
    /**
     * 添加商品到购物车列表
     *
     * @param cartList 需要添加的商品详情
     * @param itemId SKU item表的ID
     * @param num  数量
     * @return
     */
    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据SKU ID查询商品明细 SKU对象(商品明细)
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }

        if (!item.getStatus().equals("1")) {
            throw new RuntimeException("商品状态无效");
        }

        //2.根据SKU对象得到商家ID(商品所属商家)
        String sellerId = item.getSellerId();

        //3.根据商家ID在购物车列表中查询购物车对象(根据商家ID查询购物车Cart)
        Cart cart = searchCartBySellerId(cartList, sellerId);


        if (cart == null) {//4.如果购物车列表中不存在该商家的购物车
            //4.1创建一个新的购物车对象
            cart = new Cart();
            cart.setSellerId(sellerId);//商家ID
            cart.setSellerName(item.getSeller());//商家名称
            List<TbOrderItem> orderItems = new ArrayList<>();//存储购物车商品详情集合
            TbOrderItem orderItem = createOrderItem(item, num);//调用方法获取得到详情内容(内容大致都是item表中的)
            orderItems.add(orderItem);//添加到集合
            cart.setOrderItemList(orderItems);
            //4.2将新的购物车对象添加到购物车列表中
            cartList.add(cart);

        } else { //5.如果购物车列表中存在该商家的购物车
            //判断商品是否在该购物车的明细列表中存在
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            if (orderItem == null) {
                //5.1如不存在,创建新的购物车明细对象,并添加到该购物车明细列表中
                orderItem = createOrderItem(item, num);//调用下面方法,得到明细
                cart.getOrderItemList().add(orderItem);//存储进购物车

            } else {
                //5.2如果存在,在原有的数量上添加数量,并且更新金额.
                orderItem.setNum(orderItem.getNum() + num);//更改数量
                //金额
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));

                //当明细的数量小于等于0，移除此明细
                if (orderItem.getNum() <= 0) {
                    cart.getOrderItemList().remove(orderItem);
                }
                //当购物车的明细数量为0，在购物车列表中移除此购物车
                if (cart.getOrderItemList().size() == 0) {
                    cartList.remove(cart);
                }
            }
        }


        return cartList;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 购物车信息从redis中取出
     *
     * @param username 用户名
     * @return
     */

    @Override
    public List<Cart> findCartListFromRedis(String username) {
        //从redis中取值
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        System.out.println("redis取值");
        if (cartList == null) {
            cartList = new ArrayList();
        }
        return cartList;


    }

    /**
     * 购物车信息存入到redis
     *
     * @param username
     * @param cartList
     */
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("redis存值");
        redisTemplate.boundHashOps("cartList").put(username, cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        //遍历整个所有购物车
        for (Cart cart : cartList2) {
            //遍历其中每个购物车
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                //给每个购物车添加明细
                cartList1 = addGoodsToCartList(cartList1, orderItem.getItemId(), orderItem.getNum());
            }
        }


        return cartList1;
    }

    /**
     * 根据商家ID查询购物车对象
     *
     * @param cartList 购物车列表
     * @param sellerId
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {

        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {

                return cart;
            }
        }
        return null;
    }

    /**
     * 根据skuID在购物车明细列表中查询购物车明细对象
     *
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {

        for (TbOrderItem orderItem : orderItemList) {
            //getItemId和itemId都是对象,对象比较的是地址值,必须转换成基本类型longValue,才能进行值比较
            if (orderItem.getItemId().longValue() == itemId.longValue()) {
                return orderItem;
            }
        }
        return null;
    }


    /**
     * 创建购物车明细对象
     *
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        //创建新的购物车明细对象
        TbOrderItem orderItem = new TbOrderItem();
        //设置参数
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);

        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        //BigDecimal转换 Price价格转换成double格式乘以数量num
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));

        return orderItem;
    }
}
