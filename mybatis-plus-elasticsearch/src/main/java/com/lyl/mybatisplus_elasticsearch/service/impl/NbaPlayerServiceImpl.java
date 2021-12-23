package com.lyl.mybatisplus_elasticsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyl.mybatisplus_elasticsearch.dao.NbaPlayerDao;
import com.lyl.mybatisplus_elasticsearch.entity.NbaPlayer;
import com.lyl.mybatisplus_elasticsearch.service.NbaPlayerService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * (NbaPlayer)表服务实现类
 *
 * @author 罗亚龙
 * @since 2021-12-21 14:45:58
 */
@Service("nbaPlayerService")
@RequiredArgsConstructor
public class NbaPlayerServiceImpl extends ServiceImpl<NbaPlayerDao, NbaPlayer> implements NbaPlayerService {
    private final RestHighLevelClient client;

    public static final String NBA_INDEX = "nba_latest";

    @SneakyThrows
    @Override
    public List<NbaPlayer> searchMatchPrefix(String key, String value) {
        SearchRequest request = new SearchRequest(NBA_INDEX);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.prefixQuery(key, value));
        builder.from(0);
        builder.size(1000);
        request.source(builder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSON(response));
        List<NbaPlayer> playerList = new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            playerList.add(JSON.parseObject(hit.getSourceAsString(),NbaPlayer.class));
        }
        return playerList;
    }

    @SneakyThrows
    @Override
    public List<NbaPlayer> searchTerm(String key, String value) {
        SearchRequest request = new SearchRequest(NBA_INDEX);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.termQuery(key,value));
        builder.from(0);
        builder.size(1000);
        request.source(builder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSON(response));
        List<NbaPlayer> playerList = new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            playerList.add(JSON.parseObject(hit.getSourceAsString(),NbaPlayer.class));
        }
        return playerList;
    }

    @SneakyThrows
    @Override
    public List<NbaPlayer> searchMatch(String key, String value) {
        SearchRequest request = new SearchRequest(NBA_INDEX);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchQuery(key,value));
        builder.from(0);
        builder.size(1000);
        request.source(builder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSON(response));

        List<NbaPlayer> playerList = new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            playerList.add(JSON.parseObject(hit.getSourceAsString(),NbaPlayer.class));
        }
        return playerList;
    }

    @Override
    public boolean insertAll() {
        this.list()
                .forEach(
                        player ->
                                addPlayer(player, player.getId() + "")
                );
        return true;
    }

    @Override
    public boolean addPlayer(NbaPlayer player, String id) {
        IndexRequest indexRequest = new IndexRequest(NBA_INDEX)
                .id(id)
                .source(beanToMap(player));
        IndexResponse response = null;
        try {
            response = client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(JSONObject.toJSON(response));
        return false;
    }

    @Override
    public Map<String, Object> getPlayer(String id) {
        GetRequest getRequest = new GetRequest(NBA_INDEX, id);
        GetResponse response = null;
        try {
            response = client.get(getRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.getSource();
    }

    /**
     * 修改player
     *
     * @param player 球员
     * @param id     编号
     * @return true, false
     */
    @Override
    public boolean updatePlayer(NbaPlayer player, String id) {
        UpdateRequest request = new UpdateRequest(NBA_INDEX, id)
                .doc(beanToMap(player));
        try {
            UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
            System.out.println(JSONObject.toJSON(response));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deletePlayer(String id) {
        DeleteRequest request = new DeleteRequest(NBA_INDEX, id);
        DeleteResponse response = null;
        try {
            response = client.delete(request, RequestOptions.DEFAULT);
            System.out.println(JSONObject.toJSONString(response));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteAllPlayer() {
        DeleteByQueryRequest request = new DeleteByQueryRequest(NBA_INDEX);
        try {
            BulkByScrollResponse response = client.deleteByQuery(request, RequestOptions.DEFAULT);
            System.out.println(JSONObject.toJSONString(response));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 对象转map
     *
     * @param bean
     * @param <T>
     * @return
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = new HashMap<>();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                if (beanMap.get(key) != null) {
                    map.put(key + "", beanMap.get(key));
                }
            }
        }
        return map;
    }


}

