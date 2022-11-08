package com.dbxiao.galaxy.bxuser.chaincode.gateway.client;

/**
 * @author amen
 * @date 2022/11/4
 */
public enum ChannelEnum {

    URF_CC("userrolecc"),
    RRF_CC("roleprivilegecc"),
    BL_CC("blacklistcc"),
    LOG_CC("logstashcc")
    ;
    private String name;

    ChannelEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
