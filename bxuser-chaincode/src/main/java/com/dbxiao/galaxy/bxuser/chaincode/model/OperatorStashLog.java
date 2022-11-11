package com.dbxiao.galaxy.bxuser.chaincode.model;

import java.util.List;

/**
 * @author amen
 * @date 2022/10/28
 */

public class OperatorStashLog {

    private List<String> keys;
    private String indexName;
    private String logDataMd5;

    private Boolean rs;
    private Long operatorAt;
    private Long operatorId;

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getLogDataMd5() {
        return logDataMd5;
    }

    public void setLogDataMd5(String logDataMd5) {
        this.logDataMd5 = logDataMd5;
    }

    public Boolean getRs() {
        return rs;
    }

    public void setRs(Boolean rs) {
        this.rs = rs;
    }

    public Long getOperatorAt() {
        return operatorAt;
    }

    public void setOperatorAt(Long operatorAt) {
        this.operatorAt = operatorAt;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }
}
