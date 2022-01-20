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

    /**
     * 令牌桶 + 乐观锁 + 限时 版本的秒杀
     * @param id 商品id
     * @return str
     */
    String killByTimeLimit(Integer id);

    /**
     * 设置秒杀商品和过期时长
     * @param id 秒杀商品id
     * @param time 过期时长(秒)
     * @return
     */
    String setKillStockTime(Integer id, long time);

    /**
     * 获取token
     * @param id 商品id
     * @param userid 用户id
     * @return token
     */
    String getToken(Integer id, Integer userid);

    /**
     * 秒杀接口（需要携带token）
     * @param id 商品id
     * @param userid 用户id
     * @param token token
     * @return
     */
    String killByToken(Integer id, Integer userid, String token);
}
