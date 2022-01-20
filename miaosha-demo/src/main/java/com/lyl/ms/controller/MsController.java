package com.lyl.ms.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.lyl.ms.service.MsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author 罗亚龙
 * @date 2022/1/16 15:17
 */
@Slf4j
@RestController
@RequestMapping("/ms")
@RequiredArgsConstructor
public class MsController {
    private final MsService msService;

    /**
     * 创建实例：每秒种产生20个令牌
     */
    private RateLimiter limiter = RateLimiter.create(20);


    /**
     * 使用synchronized来保证不超买
     * @param id 商品id
     * @return 秒杀信息
     */
    @GetMapping("/kill")
    public String kill(Integer id){
        if (id == null){
            //这里，只是简单的校验一下，可以使用jsr303搭配全局异常处理，校验参数
            return "商品id不能为空";
        }
        return msService.kill(id);
    }

    /**
     * 乐观锁版本的秒杀
     * @param id 商品id
     * @return str
     */
    @GetMapping("/killByOptimismLock")
    public String killByOptimismLock(Integer id){
        if (id == null){
            //这里，只是简单的校验一下，可以使用jsr303搭配全局异常处理，校验参数
            return "商品id不能为空";
        }
        return msService.killByOptimismLock(id);
    }

    /**
     * 令牌桶 + 乐观锁版本的秒杀
     * @param id 商品id
     * @return str
     */
    @GetMapping("/killByOptimismLockLimit")
    public String killByOptimismLockLimit(Integer id){
        //请求在2秒钟内没有获取到令牌，会直接抛弃请求
        if (!limiter.tryAcquire(2, TimeUnit.SECONDS)){
            log.error("抢购失败：服务器进行了限流");
        }
        if (id == null){
            //这里，只是简单的校验一下，可以使用jsr303搭配全局异常处理，校验参数
            return "商品id不能为空";
        }
        return msService.killByOptimismLock(id);
    }

    /**
     * 令牌桶 + 乐观锁 + 限时 版本的秒杀
     * @param id 商品id
     * @return str
     */
    @GetMapping("/killByTimeLimit")
    public String killByTimeLimit(Integer id){
        //请求在2秒钟内没有获取到令牌，会直接抛弃请求
        if (!limiter.tryAcquire(2, TimeUnit.SECONDS)){
            log.error("抢购失败：服务器进行了限流");
        }
        if (id == null){
            //这里，只是简单的校验一下，可以使用jsr303搭配全局异常处理，校验参数
            return "商品id不能为空";
        }
        return msService.killByTimeLimit(id);
    }

    /**
     * 设置秒杀商品和过期时长
     * @param id 秒杀商品id
     * @param time 过期时长(秒)
     * @return
     */
    @GetMapping("/setKillStockTime")
    public String setKillStockTime(Integer id,long time){
        //参数校验
        return msService.setKillStockTime(id,time);
    }

    /**
     * 获取token
     * @param id 商品id
     * @param userid 用户id
     * @return token
     */
    @GetMapping("/getToken")
    public String getToken(Integer id,Integer userid){
        return msService.getToken(id,userid);
    }

    /**
     * 秒杀接口（需要携带token）
     * @param id 商品id
     * @param userid 用户id
     * @param token token
     * @return
     */
    @GetMapping("/killByToken")
    public String killByToken(@RequestParam Integer id,
                              @RequestParam Integer userid,
                              @RequestParam String token){
        //参数校验
        //请求在2秒钟内没有获取到令牌，会直接抛弃请求
        if (!limiter.tryAcquire(2, TimeUnit.SECONDS)){
            log.error("抢购失败：服务器进行了限流");
        }
        if (id == null){
            //这里，只是简单的校验一下，可以使用jsr303搭配全局异常处理，校验参数
            return "商品id不能为空";
        }
        return msService.killByToken(id,userid,token);
    }

}
