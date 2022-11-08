package com.dbxiao.galaxy.bxuser.chaincode.model;

import com.dbxiao.galaxy.bxuser.chaincode.utils.JSON;

/**
 * @author amen
 * @date 2022/10/28
 */
public class OperatorLog {
    private String service;
    private String method;
    private Long operatorId;
    private Long operatorAt;
    private String body;
    private String rs;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Long getOperatorAt() {
        return operatorAt;
    }

    public void setOperatorAt(Long operatorAt) {
        this.operatorAt = operatorAt;
    }

    public String getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = JSON.toJSONString(body);
    }

    public String getRs() {
        return rs;
    }

    public void setRs(String rs) {
        this.rs = JSON.toJSONString(rs);
    }
}
