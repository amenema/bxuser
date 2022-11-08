package com.dbxiao.galaxy.bxuser.chaincode.dto;

import java.util.List;

/**
 * @author amen
 * @date 2022/10/28
 */
public class Page {

    private Integer pageSize;
    private String bookMark;
    private List<String> dataMd5;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getBookMark() {
        return bookMark;
    }

    public void setBookMark(String bookMark) {
        this.bookMark = bookMark;
    }

    public List<String> getDataMd5() {
        return dataMd5;
    }

    public void setDataMd5(List<String> dataMd5) {
        this.dataMd5 = dataMd5;
    }
}
