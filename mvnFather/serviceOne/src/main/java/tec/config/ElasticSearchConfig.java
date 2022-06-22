package tec.config;

import lombok.Data;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

@Data
@Configuration
@ConfigurationProperties(prefix = "elasticsearch")//自动注入属性无需value注解
public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {

    private String host;
    private Integer port;

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        return new RestHighLevelClient(RestClient.builder(new HttpHost(host, port)));
    }

}
