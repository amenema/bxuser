package com.dbxiao.galaxy.bxuser.chaincode.transfer;

/**
 * @author amen
 * @date 2022/8/26
 */
public class BlackListTest {

    public static void main(String[] args) throws InterruptedException {
        String listId = "fwefa";
        int fixCount = 0;
        int slideCount = 0;
        for (int i = 0; i < 100; i++) {
            long now = System.currentTimeMillis();
            boolean slideFlag = SlideWindow.isGo(listId, 2, 100L, now);
            boolean fixFlag = FixWindow.isGo(listId, 2, 100L, now);
            if (!slideFlag) {
                slideCount += 1;
            }
            if (!fixFlag) {
                fixCount += 1;
            }
            Thread.sleep((int)(Math.random() * 100));
        }
        System.out.println(String.format("fixCount:%s, slideCount: %s", fixCount, slideCount));
    }

}
