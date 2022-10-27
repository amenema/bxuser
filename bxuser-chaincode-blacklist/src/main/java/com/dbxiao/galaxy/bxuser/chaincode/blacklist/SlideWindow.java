package com.dbxiao.galaxy.bxuser.chaincode.blacklist;

/**
 * @author amen
 * @date 2022/8/26
 */
public class SlideWindow {

    private static final Long WINDOW_OFFSET = 10000L;


    private SlideWindow() {
    }

    private static Long generatorFlag(Long currentTime) {
        long reminder = currentTime % WINDOW_OFFSET;
        return currentTime - reminder;
    }



    public static WindowData[] init(Long timeWindow, Long currentTime) {

        int size = (int) Math.ceil(timeWindow / WINDOW_OFFSET);
        WindowData[] ints = new WindowData[size];
        Long lastFlag = generatorFlag(currentTime);
        for (int i = 0; i < ints.length; i++) {
            ints[i] = new WindowData(lastFlag - (size - i - 1) * WINDOW_OFFSET);
        }
        return ints;
    }

    private static int queryIndex(WindowData[] list, Long timeWindow, Long currentTime) {
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

    public static WindowData[] addData(WindowData[] list, Long timeWindow, Long currentTime) {
        int i = queryIndex(list, timeWindow, currentTime);
        WindowData[] init = init(timeWindow, currentTime);
        if (i == -1) {
            init[init.length - 1].incr();
            return init;
        }
        for (int i1 = i; i1 < list.length; i1++) {
            init[i1 - i].setSize(list[i1].getSize());
        }
        init[init.length - 1].incr();
        return init;

    }

    private static Integer count(WindowData[] list) {
        int count = 0;
        for (int i = 0; i < list.length; i++) {
            count += list[i].getSize();
        }
        return count;
    }

    public static synchronized boolean isGo(BlackList blackList, int limit, Long timeWindow, Long nowTime) {
        WindowData[] list = blackList.getWindows();
        if (list == null || list.length <= 0) {
            list = init(timeWindow, nowTime);
        }
        WindowData[] rs = addData(list, timeWindow, nowTime);
        blackList.setWindows(rs);
        Integer currentCount = count(rs);
        return currentCount < limit;
    }


}
