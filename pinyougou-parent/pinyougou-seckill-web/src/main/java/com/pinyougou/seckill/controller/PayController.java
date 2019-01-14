package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeixinPayService weixinPayService;

    @Reference
    private SeckillOrderService seckillOrderService;

    @RequestMapping("/createNative")
    public Map createNative() {
        //1.获取用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //2.提取秒杀订单(从缓存)
        TbSeckillOrder order = seckillOrderService.searchOrderFromRedisByUserId(username);

        //3.调用微信支付接口
        if (order != null) {
            //utTradeNo 商品订单编号 TotalFee:金额                                  /*order.getMoney元字符串转成分*/
            return weixinPayService.createNative(order.getId() + "", (long) (order.getMoney().doubleValue() * 100) + "");
        } else {

            return new HashMap();
        }


    }

    /**
     * 查询订单状态
     *
     * @param out_trade_no 商品订单编号
     * @return
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        //1.获取用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Result result = null;
        int x = 0;//计时
        while (true) {
            Map<String, String> map = weixinPayService.queryPayStatus(out_trade_no);//微信返回的结果

            if (map == null) {
                result = new Result(false, "支付发生错误");

                break;
            }

            if (map.get("trade_state").equals("SUCCESS")) {//trade_state 交易状态
                result = new Result(true, "支付成功");
                //保存订单
                seckillOrderService.saveOrderFromRedisToDb(username, Long.valueOf(out_trade_no), map.get("transaction_id"));
                break;
            }

            try {
                Thread.sleep(3000);//线程睡眠3秒执行
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            x++;
            if (x == 100) {
                result = new Result(false, "二维码超时");

                Map<String, String> payResult = weixinPayService.closePay(out_trade_no);
                //不为空,且返回状态码FAIL是错误
                if (payResult != null && "FAIL".equals(payResult.get("return_code"))) {
                    //err_code错误码   如果是ORDERPAID代表订单已支付
                    if ("ORDERPAID".equals(payResult.get("err_code"))) {
                        result = new Result(true, "支付成功");
                        //保存订单
                        seckillOrderService.saveOrderFromRedisToDb(username, Long.valueOf(out_trade_no), map.get("transaction_id"));
                    }
                }
                //删除订单 isSuccess是方法 success是值
                if (result.isSuccess() == false) {

                    seckillOrderService.deleteOrderFromRedis(username, Long.valueOf(out_trade_no));
                }
                break;
            }


        }
        System.out.println(result.getMessage());
        return result;
    }


}
