package com.lyl.mybatisplus_elasticsearch.common.config;

import lombok.Data;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 罗亚龙
 * @date 2021/12/21 15:35
 */
@Data
@Configuration
//@ConfigurationProperties(prefix = "elasticsearch")
public class EsConfig {

    /**
     * ip
     */
    @Value("${elasticsearch.host}")
    private String host;

    /**
     * 端口号
     */
    @Value("${elasticsearch.port}")
    private Integer port;

    /**
     * 初始化 RestHighLevelClient
     * @return
     */
    @Bean(destroyMethod = "close")
    public RestHighLevelClient highLevelClient(){
        System.out.println("host = " + host);
        System.out.println("port = " + port);
        return new RestHighLevelClient(RestClient.builder(
                new HttpHost(host,port,"http")
        ));
    }






}
