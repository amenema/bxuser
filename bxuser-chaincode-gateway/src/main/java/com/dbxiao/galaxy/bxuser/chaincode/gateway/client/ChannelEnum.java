package com.dbxiao.galaxy.bxuser.chaincode.gateway.client;

/**
 * @author amen
 * @date 2022/11/4
 */
public enum ChannelEnum {

    URF_CC("userRoleCC","userrolecc"),
    RRF_CC("roleRefCC","roleprivilegecc"),
    BL_CC("blacklistCC","blacklistcc"),
    LOG_CC("logstashCC","logstashcc")
    ;
    private String contract;
    private String chaincode;

    ChannelEnum(String contract, String chaincode) {
        this.contract = contract;
        this.chaincode = chaincode;
    }

    public String getContract() {
        return contract;
    }

    public String getChaincode() {
        return chaincode;
    }
}
