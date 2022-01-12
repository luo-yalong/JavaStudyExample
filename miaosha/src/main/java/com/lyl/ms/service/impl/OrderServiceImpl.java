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
        Stock stock = checkStock(id);
        //扣除库存
        reduceStockOfLuckLock(stock);
        //创建订单
        StockOrder order = createOrder(stock);
        return order.getId();
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
