package com.lyl.ms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyl.ms.dao.StockOrderDao;
import com.lyl.ms.entity.StockOrder;
import com.lyl.ms.service.StockOrderService;
import org.springframework.stereotype.Service;

/**
 * (StockOrder)表服务实现类
 *
 * @author makejava
 * @since 2022-01-11 20:15:27
 */
@Service("stockOrderService")
public class StockOrderServiceImpl extends ServiceImpl<StockOrderDao, StockOrder> implements StockOrderService {

}

