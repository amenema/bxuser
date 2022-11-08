package com.dbxiao.galaxy.bxuser.chaincode.model;

import java.util.List;

/**
 * @author amen
 * @date 2022/10/28
 */

public class OperatorStashLog {

    private List<String> keysMd5;
    private String logData;
    private String logDataMd5;

    private Long operatorAt;

    public Long getOperatorAt() {
        return operatorAt;
    }

    public void setOperatorAt(Long operatorAt) {
        this.operatorAt = operatorAt;
    }

    public List<String> getKeysMd5() {
        return keysMd5;
    }

    public void setKeysMd5(List<String> keysMd5) {
        this.keysMd5 = keysMd5;
    }

    public String getLogData() {
        return logData;
    }

    public void setLogData(String logData) {
        this.logData = logData;
    }

    public String getLogDataMd5() {
        return logDataMd5;
    }

    public void setLogDataMd5(String logDataMd5) {
        this.logDataMd5 = logDataMd5;
    }
}
