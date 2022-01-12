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
     * 销售商品
     * @param stock 商品信息
     * @return 影响的行数
     */
    int sale(Stock stock);
}

