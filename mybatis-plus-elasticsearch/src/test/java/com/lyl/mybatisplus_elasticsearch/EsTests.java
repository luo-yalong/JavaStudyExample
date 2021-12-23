package com.lyl.mybatisplus_elasticsearch;

import com.lyl.mybatisplus_elasticsearch.entity.NbaPlayer;
import com.lyl.mybatisplus_elasticsearch.service.NbaPlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

/**
 * @author 罗亚龙
 * @date 2021/12/22 16:13
 */
@SpringBootTest
public class EsTests {

    @Autowired
    private NbaPlayerService service;

    /**
     * 将数据库中的数据放入到es中
     */
    @Test
    void insertEsDataByMySQL(){
        System.out.println("service.insertAll() = " + service.insertAll());
    }

    /**
     * 删除单个
     */
    @Test
    void testDeleteOne(){
        service.deletePlayer("997");
    }

    /**
     * 删除全部
     */
    @Test
    void testDeleteAll(){
        service.deleteAllPlayer();
    }

    /**
     * 测试向es中添加一条数据
     */
    @Test
    void testAddPlayer(){
        NbaPlayer nbaPlayer = new NbaPlayer();
        nbaPlayer.setId(997)
                .setDisplayName("王五");
        service.addPlayer(nbaPlayer, nbaPlayer.getId() + "");
    }

    /**
     * 通过查询es中的一条数据
     */
    @Test
    void testQueryESById(){
        Map<String, Object> player = service.getPlayer("997");
        System.out.println(player);
    }

    /**
     * 更新
     */
    @Test
    void testUpdatePlayer(){
        NbaPlayer nbaPlayer = new NbaPlayer();
        nbaPlayer.setId(997)
                .setDisplayName("王小虎");
        service.updatePlayer(nbaPlayer,nbaPlayer.getId() + "");
    }

}
