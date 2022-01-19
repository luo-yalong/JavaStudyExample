package com.lyl.ms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyl.ms.dao.StockOrderDao;
import com.lyl.ms.entity.StockOrder;
import com.lyl.ms.service.StockOrderService;
import org.springframework.stereotype.Service;

/**
 * (StockOrder)表服务实现类
 *
 * @author 罗亚龙
 * @since 2022-01-16 14:15:05
 */
@Service("stockOrderService")
public class StockOrderServiceImpl extends ServiceImpl<StockOrderDao, StockOrder> implements StockOrderService {

}

