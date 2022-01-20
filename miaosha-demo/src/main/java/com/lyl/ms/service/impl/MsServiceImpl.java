package com.lyl.ms.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lyl.ms.common.exeception.MyException;
import com.lyl.ms.dao.StockDao;
import com.lyl.ms.entity.Stock;
import com.lyl.ms.entity.StockOrder;
import com.lyl.ms.service.MsService;
import com.lyl.ms.service.StockOrderService;
import com.lyl.ms.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import sun.rmi.runtime.Log;

import java.util.concurrent.TimeUnit;

/**
 * @author 罗亚龙
 * @date 2022/1/16 15:24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MsServiceImpl implements MsService {
    private final StockDao stockDao;
    private final StockOrderService orderService;
    private final RedisTemplate<String, Object> redisTemplate;


    @Override
    public synchronized String kill(Integer id) {
        //校验库存
        Stock stock = checkStock(id);
        //扣除库存
        stock.setSale(stock.getSale() + 1);
        stockDao.updateById(stock);

        //生成订单
        StockOrder order = new StockOrder();
        order.setSid(stock.getId())
                .setName(stock.getName());
        orderService.save(order);
        log.info("秒杀成功,生成的订单号为：{}", order.getId());
        return "秒杀成功,生成的订单号为：" + order.getId();
    }

    @Override
    public String killByOptimismLock(Integer id) {
        //校验库存
        Stock stock = checkStock(id);
        //扣除库存
        stock.setSale(stock.getSale() + 1);
        int i = stockDao.updateById(stock);
        if (i == 0) {
            log.error("秒杀失败: 乐观锁更新失败");
            return "秒杀失败: 乐观锁更新失败";
        }

        //生成订单
        StockOrder order = new StockOrder();
        order.setSid(stock.getId())
                .setName(stock.getName());
        orderService.save(order);
        log.info("秒杀成功,生成的订单号为：{}", order.getId());
        return "秒杀成功,生成的订单号为：" + order.getId();
    }

    @Override
    public String killByTimeLimit(Integer id) {
        if (!redisTemplate.hasKey("stock:" + id)) {
            log.error("秒杀失败: 秒杀活动已经结束了");
            return "秒杀失败: 秒杀活动已经结束了";
        }
        //校验库存
        Stock stock = checkStock(id);
        //扣除库存
        stock.setSale(stock.getSale() + 1);
        int i = stockDao.updateById(stock);
        if (i == 0) {
            log.error("秒杀失败: 乐观锁更新失败");
            return "秒杀失败: 乐观锁更新失败";
        }

        //生成订单
        StockOrder order = new StockOrder();
        order.setSid(stock.getId())
                .setName(stock.getName());
        orderService.save(order);
        log.info("秒杀成功,生成的订单号为：{}", order.getId());
        return "秒杀成功,生成的订单号为：" + order.getId();
    }

    @Override
    public String setKillStockTime(Integer id, long time) {
        redisTemplate.opsForValue().set("stock:" + id, true, time, TimeUnit.SECONDS);
        return "成功设置商品的秒杀时间";
    }

    @Override
    public String getToken(Integer id, Integer userid) {
        //校验商品是否存在，是否参与秒杀
        //校验用户是否存在，是否有秒杀的资格

        //生成token 加盐
        String salt = "#@$%#@!";

        //生成token
        String token = SecureUtil.md5(System.currentTimeMillis() + "_" + userid + "_" + salt);
        String key = "ms:token:" + id + ":" + userid;
        //放在redis中，并设置过期时间
        redisTemplate.opsForValue().set(key, token, 5 * 60, TimeUnit.SECONDS);
        return token;
    }

    @Override
    public String killByToken(Integer id, Integer userid, String token) {
        //校验请求的合法性
        String key = "ms:token:" + id + ":" + userid;
        String oldToken = (String) redisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(oldToken)
                || (StrUtil.isNotBlank(oldToken) && !oldToken.equals(token))) {
            log.error("请求不合法或者token过期");
            return "请求不合法或者token过期";
        }


        if (!redisTemplate.hasKey("stock:" + id)) {
            log.error("秒杀失败: 秒杀活动已经结束了");
            return "秒杀失败: 秒杀活动已经结束了";
        }
        //校验库存
        Stock stock = checkStock(id);
        //扣除库存
        stock.setSale(stock.getSale() + 1);
        int i = stockDao.updateById(stock);
        if (i == 0) {
            log.error("秒杀失败: 乐观锁更新失败");
            return "秒杀失败: 乐观锁更新失败";
        }

        //生成订单
        StockOrder order = new StockOrder();
        order.setSid(stock.getId())
                .setName(stock.getName());
        orderService.save(order);
        log.info("秒杀成功,生成的订单号为：{}", order.getId());
        return "秒杀成功,生成的订单号为：" + order.getId();
    }

    /**
     * 校验库存
     *
     * @param id 商品id
     * @return 商品
     */
    private Stock checkStock(Integer id) {
        Stock stock = stockDao.selectById(id);
        if (stock == null) {
            throw new MyException("秒杀商品 id=" + id + "不存在");
        }
        if (stock.getSale() - stock.getCount() >= 0) {
            throw new MyException("秒杀失败:  库存不足");
        }
        return stock;
    }
}
