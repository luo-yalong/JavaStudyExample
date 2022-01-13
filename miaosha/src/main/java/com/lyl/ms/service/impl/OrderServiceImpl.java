package com.lyl.ms.service.impl;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lyl.ms.dao.StockDao;
import com.lyl.ms.dao.StockOrderDao;
import com.lyl.ms.dao.UserDao;
import com.lyl.ms.entity.Stock;
import com.lyl.ms.entity.StockOrder;
import com.lyl.ms.entity.User;
import com.lyl.ms.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    @Resource
    private StockDao stockDao;
    @Resource
    private StockOrderDao stockOrderDao;
    @Resource
    private UserDao userDao;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public int kill(Integer id) {
        if (!redisTemplate.hasKey("stock::" + id)){
            throw new RuntimeException("很抱歉，当前秒杀活动已经结束了");
        }
        //校验库存（根据商品id）
        Stock stock = checkStock(id);
        //扣除库存
        reduceStockOfLuckLock(stock);
        //创建订单
        StockOrder order = createOrder(stock);
        return order.getId();
    }

    @Override
    public int killTokenMd5(Integer id, Integer userid, String md5) {
        //接口的限时操作
        if (!redisTemplate.hasKey("stock::" + id)){
            throw new RuntimeException("很抱歉，当前秒杀活动已经结束了");
        }

        //接口防刷-使用md5验证
        String key = "ms:" + userid + ":" + id;
        if (!md5.equals(redisTemplate.opsForValue().get(key))) {
            throw new RuntimeException("当前请求数据不合法");
        }


        //校验库存（根据商品id）
        Stock stock = checkStock(id);
        //扣除库存
        reduceStockOfLuckLock(stock);
        //创建订单
        StockOrder order = createOrder(stock);
        return order.getId();
    }

    @Override
    public String getMd5(Integer id, Integer userid) {
        //校验用户
        checkUserWhetherExists(userid);
        //校验商品
        checkStockWhetherExists(id);
        //生成md5，存入redis,同时返回客户端
        //键
        String key = "ms:" + userid + ":" + id;
        String md5 = SecureUtil.md5((id + userid + "#!@$%"));
        //五分钟内有效
        redisTemplate.opsForValue().set(key,md5,5 * 60 , TimeUnit.SECONDS);
        return "生成的token: " + md5;
    }

    private void checkUserWhetherExists(Integer userid) {

        User user = userDao.selectById(userid);
        if (user == null) {
            throw new RuntimeException("当前用户不存在");
        }
    }

    private void checkStockWhetherExists(Integer id) {
        Stock stock = stockDao.selectById(id);
        if (stock == null){
            throw new RuntimeException("当前商品不存在");
        }
    }

    /**
     * 生成订单
     * @param stock 商品信息
     * @return 订单信息
     */
    private StockOrder createOrder(Stock stock) {
        StockOrder order = new StockOrder()
                .setSid(stock.getId())
                .setName(stock.getName());
        stockOrderDao.insert(order);
        return order;
    }

    /**
     * 减少库存
     * @param stock 商品信息
     */
    private void reduceStock(Stock stock) {
        stock.setSale(stock.getSale() + 1);
        stockDao.update(stock,new LambdaUpdateWrapper<Stock>()
                .set(Stock::getSale, stock.getSale())
                .eq(Stock::getId, stock.getId())
        );
    }

    /**
     * 减少库存
     * @param stock 商品信息
     */
    private void reduceStockOfLuckLock(Stock stock) {
        int updateRows = stockDao.sale(stock);
        if (updateRows == 0){
            throw new RuntimeException("version过期");
        }
    }

    /**
     * 校验库存
     * @param id 商品id
     * @return 商品信息
     */
    private Stock checkStock(Integer id) {
        Stock stock = stockDao.selectById(id);
        if (stock.getSale().equals(stock.getCount())) {
            throw new RuntimeException("库存不足");
        }
        return stock;
    }
}
