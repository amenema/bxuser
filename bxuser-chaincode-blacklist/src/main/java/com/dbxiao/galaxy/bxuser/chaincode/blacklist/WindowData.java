package com.dbxiao.galaxy.bxuser.chaincode.blacklist;

public class WindowData {
    private Integer size = 0;
    private Long timeFlag;


    public WindowData(Integer size, Long timeFlag) {
        this.size = size;
        this.timeFlag = timeFlag;
    }

    public WindowData(Long timeFlag) {
        this.size = 0;
        this.timeFlag = timeFlag;
    }

    public void incr(){
        size += 1;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getTimeFlag() {
        return timeFlag;
    }

    public void setTimeFlag(Long timeFlag) {
        this.timeFlag = timeFlag;
    }
}