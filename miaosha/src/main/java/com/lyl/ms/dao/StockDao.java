package com.lyl.ms.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lyl.ms.entity.Stock;

/**
 * (Stock)表数据库访问层
 *
 * @author makejava
 * @since 2022-01-11 20:15:27
 */
public interface StockDao extends BaseMapper<Stock> {

    /**
     * 售出商品
     * @param stock 商品
     * @return 影响行数
     */
    int saleStock(Stock stock);
}

