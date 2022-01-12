package com.lyl.ms.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.lyl.ms.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("stock")
public class StockController {
    @Resource
    private OrderService orderService;
    //创建实例：每秒种产生20个令牌
    private RateLimiter limiter = RateLimiter.create(20);

    @GetMapping("/sale")
    public String sale(){
        //获取到令牌的请求，执行业务方法，没有获取到令牌的请求，直到获取到令牌，执行业务方法
        //log.info("令牌等待时间: " + limiter.acquire());

        //请求在2秒钟内没有获取到令牌，会直接抛弃请求
        if (!limiter.tryAcquire(3, TimeUnit.SECONDS)){
            log.info("抢购失败：服务器进行了限流");
        }

        log.info("执行业务");
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "成功";
    }

    @GetMapping("/killToken")
    public String killToken(Integer id){

        if (!limiter.tryAcquire(3, TimeUnit.SECONDS)){
            log.info("抢购失败：活动太火爆了，请重试！！！");
        }

        System.out.println("秒杀商品的id = " + id);
        //处理秒杀业务逻辑
        try {
            int orderId = orderService.kill(id);
            log.info("秒杀成功：订单ID = " + orderId);
            return "秒杀成功：订单ID = " + orderId;
        } catch (Exception e) {
            //e.printStackTrace();
            log.info("抢购失败：" + e.getMessage());
            return e.getMessage();
        }
    }

    @GetMapping("/kill")
    public String kill(Integer id){
        System.out.println("秒杀商品的id = " + id);
        //处理秒杀业务逻辑
        try {
            int orderId = orderService.kill(id);
            log.info("秒杀成功：订单ID = " + orderId);
            return "秒杀成功：订单ID = " + orderId;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

}
