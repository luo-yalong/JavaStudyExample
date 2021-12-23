package com.lyl.mybatisplus_elasticsearch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lyl.mybatisplus_elasticsearch.entity.NbaPlayer;

import java.util.List;
import java.util.Map;

/**
 * (NbaPlayer)表服务接口
 *
 * @author 罗亚龙
 * @since 2021-12-21 14:45:58
 */
public interface NbaPlayerService extends IService<NbaPlayer> {

    List<NbaPlayer> searchMatchPrefix(String key,String value);


    List<NbaPlayer> searchTerm(String key,String value);

    /**
     * 通过名字搜索
     * @param key
     * @param value
     * @return
     */
    List<NbaPlayer> searchMatch(String key,String value);

    /**
     * 批量插入es
     * @return ture,false
     */
    boolean insertAll();

    /**
     * 添加球员
     * @param player 球员
     * @param id id
     * @return true/false
     */
    boolean addPlayer(NbaPlayer player,String id);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    Map<String,Object> getPlayer(String id);

    boolean updatePlayer(NbaPlayer player,String id);

    boolean deletePlayer(String id);

    boolean deleteAllPlayer();

}

