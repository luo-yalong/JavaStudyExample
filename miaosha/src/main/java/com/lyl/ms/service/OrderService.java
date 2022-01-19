package com.lyl.ms.service;

public interface OrderService {

    /**
     * 处理秒杀相关的业务逻辑
     * @param id 商品id
     * @return 订单id
     */
    int kill(Integer id);

    /**
     * 带校验的秒杀操作
     * @param id
     * @param userid
     * @param md5
     * @return
     */
    int killTokenMd5(Integer id, Integer userid, String md5);

    /**
     * 获取md5
     * @param id 商品id
     * @param userid 用户id
     * @return
     */
    String getMd5(Integer id, Integer userid);
}
