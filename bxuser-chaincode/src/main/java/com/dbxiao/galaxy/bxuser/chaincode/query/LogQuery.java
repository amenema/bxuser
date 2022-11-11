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

    private Long operatorId;
    private String indexName;
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

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }
}
