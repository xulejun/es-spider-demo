package com.xlj.esspider.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ES配置
 * @Author XLJ
 * @Date 2020/8/12
 */
@Configuration
public class ElasticSearchConfig {
    /**
     * @description: ES高级客户端配置
     * @author XLJ
     * @date 2020/8/12 17:33
    */
    @Bean
    public RestHighLevelClient restHighLevelClient(){
        HttpHost httpHost = new HttpHost("127.0.0.1", 9200, "http");
        RestClientBuilder builder = RestClient.builder(httpHost);
        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }
}
