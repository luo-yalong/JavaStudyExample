package com.lyl.ms.service.impl;

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
import org.springframework.stereotype.Service;
import sun.rmi.runtime.Log;

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
        log.info("秒杀成功,生成的订单号为：{}" , order.getId());
        return "秒杀成功,生成的订单号为：" + order.getId();
    }

    @Override
    public String killByOptimismLock(Integer id) {
        //校验库存
        Stock stock = checkStock(id);
        //扣除库存
        stock.setSale(stock.getSale() + 1);
        int i = stockDao.updateById(stock);
        if (i == 0){
            log.error("秒杀失败: 乐观锁更新失败");
            return "秒杀失败: 乐观锁更新失败";
        }

        //生成订单
        StockOrder order = new StockOrder();
        order.setSid(stock.getId())
                .setName(stock.getName());
        orderService.save(order);
        log.info("秒杀成功,生成的订单号为：{}" , order.getId());
        return "秒杀成功,生成的订单号为：" + order.getId();
    }

    /**
     * 校验库存
     * @param id 商品id
     * @return 商品
     */
    private Stock checkStock(Integer id) {
        Stock stock = stockDao.selectById(id);
        if (stock == null){
            throw new MyException("秒杀商品 id=" + id + "不存在");
        }
        if (stock.getSale() - stock.getCount() >= 0){
            throw new MyException("秒杀失败:  库存不足");
        }
        return stock;
    }
}
