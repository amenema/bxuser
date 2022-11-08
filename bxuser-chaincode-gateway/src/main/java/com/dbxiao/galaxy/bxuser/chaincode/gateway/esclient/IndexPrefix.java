package com.dbxiao.galaxy.bxuser.chaincode.gateway.esclient;

/**
 * @author amen
 * @date 2021/1/26 4:14 下午
 */
public enum IndexPrefix {
    DEV("dev"),
    TEST("test"),
    PROD("prod"),
    ;

    private String prefix;

    IndexPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String prefix() {
        return prefix;
    }
}
