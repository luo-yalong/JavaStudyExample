package com.lyl.ms.service;

public interface OrderService {

    /**
     * 处理秒杀相关的业务逻辑
     * @param id 商品id
     * @return 订单id
     */
    int kill(Integer id);
}
