package com.lyl.ms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyl.ms.dao.StockDao;
import com.lyl.ms.entity.Stock;
import com.lyl.ms.service.StockService;
import org.springframework.stereotype.Service;

/**
 * (Stock)表服务实现类
 *
 * @author makejava
 * @since 2022-01-11 20:15:27
 */
@Service("stockService")
public class StockServiceImpl extends ServiceImpl<StockDao, Stock> implements StockService {

}

