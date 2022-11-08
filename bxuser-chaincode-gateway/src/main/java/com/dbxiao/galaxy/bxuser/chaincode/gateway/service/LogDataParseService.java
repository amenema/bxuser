package com.dbxiao.galaxy.bxuser.chaincode.gateway.service;

import com.dbxiao.galaxy.bxuser.chaincode.gateway.esclient.EsClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author amen
 * @date 2022/11/4
 */
@Slf4j
@Service
public class LogDataParseService {

    private static final String INDEX_NAME = "aut_log_data";
    private static final String TYPE = "_doc";

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private EsClientConfig esClientConfig;

    public void create(String logData) {

        String indexName = esClientConfig.getRealIndexName(INDEX_NAME);
        IndexRequest indexRequest = new IndexRequest(indexName, TYPE);
        indexRequest.source(logData);
        try {
            IndexResponse response = restHighLevelClient.index(indexRequest, esClientConfig.getCommonOptions());
        } catch (IOException e) {
            log.error("insert doc  into index {}  error {}",  indexName, e);
        }

    }
}
