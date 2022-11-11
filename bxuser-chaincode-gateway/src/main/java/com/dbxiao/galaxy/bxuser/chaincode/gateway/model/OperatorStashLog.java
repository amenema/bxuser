package com.dbxiao.galaxy.bxuser.chaincode.gateway.model;

import lombok.Data;

import java.util.List;

/**
 * @author amen
 * @date 2022/11/10
 */
@Data
public class OperatorStashLog {
    private List<String> keys;
    private String indexName;
    private String logDataMd5;

    private Boolean rs;
    private Long operatorAt;
    private Long operatorId;

}
