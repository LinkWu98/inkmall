package cn.link.inkmall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @Author: Link
 * @Date: 2020/5/20 21:50
 * @Version 1.0
 */
@Configuration
public class ElasticSearchConfig {

    @Bean
    @Primary
    public RestHighLevelClient getRestHighLevelClient() {
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("192.168.153.188", 9200, "http")));
    }

}
