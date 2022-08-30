package com.dbxiao.galaxy.bxuser.chaincode.transfer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author amen
 * @date 2022/8/26
 */
public class FixWindow {

    private static final Map<String, CheckData> MAP = new ConcurrentHashMap<>();

    public static synchronized boolean isGo(String listId, int count, Long timeWindow, Long nowTime) {
        CheckData checkData = MAP.computeIfAbsent(listId, k -> new CheckData(nowTime));
        Long joinAt = checkData.getJoinAt();
        if (nowTime - joinAt > timeWindow) {
            checkData.setJoinAt(nowTime);
            checkData.setCount(1);
        } else {
            checkData.setCount(checkData.getCount() + 1);
        }
        return checkData.getCount() < count;

    }

    public static class CheckData {
        private Integer count = 0;
        private Long joinAt;

        public CheckData(Long joinAt) {
            this.joinAt = joinAt;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Long getJoinAt() {
            return joinAt;
        }

        public void setJoinAt(Long joinAt) {
            this.joinAt = joinAt;
        }
    }
}
