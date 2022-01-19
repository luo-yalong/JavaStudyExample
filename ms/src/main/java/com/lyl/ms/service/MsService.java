package com.lyl.ms.service;

/**
 * @author 罗亚龙
 * @date 2022/1/16 15:24
 */
public interface MsService {

    /**
     * 秒杀方法
     * @param id 商品id
     * @return 提示信息
     */
    String kill(Integer id);

    /**
     * 乐观锁版本的秒杀
     * @param id 商品id
     * @return str
     */
    String killByOptimismLock(Integer id);
}
