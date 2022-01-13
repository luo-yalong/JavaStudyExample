package com.lyl.ms.controller;

import cn.hutool.core.util.StrUtil;
import com.google.common.util.concurrent.RateLimiter;
import com.lyl.ms.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("stock")
public class StockController {
    @Resource
    private OrderService orderService;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
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

    /**
     * 令牌桶限流，接口限时操作
     * @param id
     * @return
     */
    @GetMapping("/killToken")
    public String killToken(Integer id){
        //令牌桶限流
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

    /**
     * 用户获取md5
     * @param id 商品id
     * @param userid 用户id
     * @return
     */
    @GetMapping("/md5")
    public String getMd5(Integer id,Integer userid){
        //参数校验
        return orderService.getMd5(id,userid);
    }

    /**
     * 令牌桶限流，接口限时操作以及接口隐藏
     * @param id
     * @param userid
     * @param md5
     * @return
     */
    @GetMapping("/killTokenMd5")
    public String killTokenMd5(Integer id,Integer userid,String md5){

        if(StrUtil.isBlank(md5)){
            return "请求没有携带签名";
        }

        //令牌桶限流
        if (!limiter.tryAcquire(3, TimeUnit.SECONDS)){
            log.info("抢购失败：活动太火爆了，请重试！！！");
        }

        System.out.println("秒杀商品的id = " + id);
        //处理秒杀业务逻辑
        try {
            int orderId = orderService.killTokenMd5(id,userid,md5);
            log.info("秒杀成功：订单ID = " + orderId);
            return "秒杀成功：订单ID = " + orderId;
        } catch (Exception e) {
            //e.printStackTrace();
            log.info("抢购失败：" + e.getMessage());
            return e.getMessage();
        }
    }

    /**
     * 令牌桶限流，接口限时操作以及接口隐藏
     * @param id
     * @param userid
     * @param md5
     * @return
     */
    @GetMapping("/killTokenMd5Limit")
    public String killTokenMd5Limit(Integer id,Integer userid,String md5){

        if(StrUtil.isBlank(md5)){
            return "请求没有携带签名";
        }

        //令牌桶限流
        if (!limiter.tryAcquire(3, TimeUnit.SECONDS)){
            log.info("抢购失败：活动太火爆了，请重试！！！");
        }

        System.out.println("秒杀商品的id = " + id);
        //处理秒杀业务逻辑
        try {
            //单用户请求限制
            killLimit(id,userid);

            int orderId = orderService.killTokenMd5(id,userid,md5);
            log.info("秒杀成功：订单ID = " + orderId);
            return "秒杀成功：订单ID = " + orderId;
        } catch (Exception e) {
            //e.printStackTrace();
            log.info("抢购失败：" + e.getMessage());
            return e.getMessage();
        }
    }

    //单用户请求限制
    private void killLimit(Integer id,Integer userid){
        String key = "ms:limit:" + id + ":" + userid;
        int n = (int) Optional.ofNullable(redisTemplate.opsForValue().get(key))
                .orElse(0);
        if (n == 0){
            redisTemplate.opsForValue().set(key,1,3600,TimeUnit.SECONDS);
        }else {
            n = n + 1;
            redisTemplate.opsForValue().set(key,n,3600,TimeUnit.SECONDS);
        }
        log.info("当前用户已经请求了: {}次",n);
        if (n > 10){
            throw new RuntimeException("单位时间内达到抢购次数上限");
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
