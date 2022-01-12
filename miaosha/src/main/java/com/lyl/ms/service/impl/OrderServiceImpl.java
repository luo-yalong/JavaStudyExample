package com.lyl.ms.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lyl.ms.dao.StockDao;
import com.lyl.ms.dao.StockOrderDao;
import com.lyl.ms.entity.Stock;
import com.lyl.ms.entity.StockOrder;
import com.lyl.ms.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    @Resource
    private StockDao stockDao;
    @Resource
    private StockOrderDao stockOrderDao;

    @Override
    public int kill(Integer id) {
        //校验库存（根据商品id）
        Stock stock = stockDao.selectById(id);
        if (stock.getSale().equals(stock.getCount())) {
            throw new RuntimeException("库存不足");
        }
        //扣除库存
        stock.setSale(stock.getSale() + 1);
        stockDao.update(stock,new LambdaUpdateWrapper<Stock>()
                .set(Stock::getSale,stock.getSale())
                .eq(Stock::getId,stock.getId())
        );

        //创建订单
        StockOrder order = new StockOrder()
                .setSid(stock.getId())
                .setName(stock.getName());
        stockOrderDao.insert(order);
        return order.getId();
    }
}
