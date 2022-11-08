package com.dbxiao.galaxy.bxuser.chaincode.query;

import java.util.List;

/**
 * @author amen
 * @date 2022/10/28
 */
public class LogQuery {

    private String bookMark = "";
    private Integer pageSize;
    private List<String> matchKeys;

    public String getBookMark() {
        return bookMark;
    }

    public void setBookMark(String bookMark) {
        this.bookMark = bookMark;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<String> getMatchKeys() {
        return matchKeys;
    }

    public void setMatchKeys(List<String> matchKeys) {
        this.matchKeys = matchKeys;
    }
}
