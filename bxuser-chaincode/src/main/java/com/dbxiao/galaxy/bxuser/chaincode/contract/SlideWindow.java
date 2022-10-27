package com.dbxiao.galaxy.bxuser.chaincode.contract;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author amen
 * @date 2022/8/26
 */
public class SlideWindow {

    private volatile static Map<String, WindowData[]> MAP = new ConcurrentHashMap<>();
    private static final Long WINDOW_OFFSET = 10L;
    private static final Integer WINDOW_SIZE = 10;
    private static final Map<Long, Integer> WINDOWS_LIST = new TreeMap<>();

    public static void main(String[] args) {

    }

    private SlideWindow() {
    }

    private static Long generatorFlag(Long currentTime) {
        long reminder = currentTime % WINDOW_OFFSET;
        return currentTime - reminder;
    }

    private static WindowData[] init(Long timeWindow, Long currentTime) {

        int size = (int) Math.ceil(timeWindow / WINDOW_OFFSET);
        WindowData[] ints = new WindowData[size];
        Long lastFlag = generatorFlag(currentTime);
        for (int i = 0; i < ints.length; i++) {
            ints[i] = new WindowData(lastFlag - (size - i - 1) * WINDOW_OFFSET);
        }
        return ints;
    }

    private static int querryIndex(WindowData[] list, Long timeWindow, Long currentTime) {
        Long currentFlag = generatorFlag(currentTime);
        int size = (int) Math.ceil(timeWindow / WINDOW_OFFSET);
        Long firstFlag = currentFlag - (size - 1) * WINDOW_OFFSET;
        for (int i = 0; i < list.length; i++) {
            if (list[i].getTimeFlag().equals(firstFlag)) {
                return i;
            }
        }
        return -1;
    }

    private static WindowData[] addData(WindowData[] list, Long timeWindow, Long currentTime) {
        int i = queryIndex(list, timeWindow, currentTime);
        WindowData[] init = init(timeWindow, currentTime);
        if (i == -1) {
            init[init.length - 1].incr();
            return init;
        }
        for (int i1 = i; i1 < list.length; i1++) {
            init[i1 - i].setSize(list[i1].getSize());
        }
        init[init.length -1].incr();
        return init;

    }

    private static Integer count(WindowData[] list) {
        int count = 0;
        for (int i = 0; i < list.length; i++) {
            count += list[i].size;
        }
        return count;
    }

    public static synchronized boolean isGo(String listId, int limit, Long timeWindow, Long nowTime) {
        WindowData[] list = MAP.computeIfAbsent(listId, k -> init(timeWindow, nowTime));
        WindowData[] rs = addData(list, timeWindow, nowTime);
        MAP.put(listId, rs);
        Integer currentCount = count(rs);
        return currentCount < limit;
    }


    private static class WindowData {
        private Integer size;
        private Long timeFlag;

        public WindowData(Long timeFlag) {
            this.size = 0;
            this.timeFlag = timeFlag;
        }

        public Integer getSize() {
            return size;
        }

        public void incr() {
            size += 1;
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

}
