package com.dbxiao.galaxy.bxuser.chaincode.model;

/**
 * @author amen
 * @date 2022/11/1
 */
public class ResponseData<T> {
    private T data;
    private String logDta;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getLogDta() {
        return logDta;
    }

    public void setLogDta(String logDta) {
        this.logDta = logDta;
    }

    public static <T> ResponseData<T> success(T data){
        ResponseData<T> rd = new ResponseData<>();
        rd.setData(data);
        return rd;
    }
    public static <T> ResponseData<T> success(T data,String log){
        ResponseData<T> rd = new ResponseData<>();
        rd.setData(data);
        rd.setLogDta(log);
        return rd;
    }
}
