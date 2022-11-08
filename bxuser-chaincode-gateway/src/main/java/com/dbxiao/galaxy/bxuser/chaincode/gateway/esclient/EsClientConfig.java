package com.dbxiao.galaxy.bxuser.chaincode.gateway.esclient;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author amen
 * @date 2021/1/26 3:41 下午
 */
@Configuration
public class EsClientConfig {


    @Value("${es.username}")
    private String username;
    @Value("${es.password}")
    private String password;
    @Value("${es.host}")
    private String host;
    @Value("${es.env}")
    private String env;


    private static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        // 默认缓存限制为100MB，此处修改为50MB。
        builder.setHttpAsyncResponseConsumerFactory(
                new HttpAsyncResponseConsumerFactory
                        .HeapBufferedResponseConsumerFactory(50 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }


    public RequestOptions getCommonOptions() {
        return COMMON_OPTIONS;
    }

    public boolean isProd(){
        return IndexPrefix.PROD.name().equals(env);
    }

    public String getRealIndexName(String indexName) {
        if (env == null || env.length() <= 0) {
            return IndexPrefix.DEV.prefix();
        }
        IndexPrefix prefix = IndexPrefix.valueOf(env);
        return String.format("%s_%s", prefix.prefix(), indexName);
    }

    @Bean(destroyMethod = "close")
    public RestHighLevelClient restHighLevelClient() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        RestClientBuilder builder = RestClient.builder(new HttpHost(host, 9200, "http"))
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                                .setDefaultIOReactorConfig(
                                        IOReactorConfig.custom().setIoThreadCount(20).setSoKeepAlive(true).build()
                                ));
        RestHighLevelClient highClient = new RestHighLevelClient(builder);
        return highClient;
    }


}