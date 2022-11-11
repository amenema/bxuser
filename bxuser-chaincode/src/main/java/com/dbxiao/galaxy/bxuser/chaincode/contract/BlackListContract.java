package com.dbxiao.galaxy.bxuser.chaincode.contract;

import com.dbxiao.galaxy.bxuser.chaincode.model.BlackList;
import com.dbxiao.galaxy.bxuser.chaincode.model.SlideWindow;
import com.dbxiao.galaxy.bxuser.chaincode.model.WindowData;
import com.dbxiao.galaxy.bxuser.chaincode.utils.JSON;
import org.hyperledger.fabric.Logger;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.HashMap;
import java.util.Map;

/**
 * @author amen
 * @date 2022/7/22
 */
@Default
@Contract(name = "blacklistcc", info = @Info(title = "BXUser BlackListContract",
        description = "BXUser BlackListContract", version = "1.0"))
public class BlackListContract implements ContractInterface {
    private static final String BLACK_LIST_KEY_FORMAT = "blacklist_%s";

    private static final Long WINDOWS_TIME_LENGTH = 5 * 60 * 1000L;
    private static final Integer LIMIT = 10;
    static Map<String, String> MAP = new HashMap<String, String>();

    private static final Logger LOGGER = Logger.getLogger("BlackListContract");

    private static final String buildKey(Long key) {
        return String.format(BLACK_LIST_KEY_FORMAT, key);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public BlackList query(final Context ctx, final Long userId) {
        ChaincodeStub stub = ctx.getStub();

        String realKey = buildKey(userId);
        String valueState = stub.getStringState(realKey);
        if (valueState == null || valueState.length() <= 0) {
            return new BlackList();
        }
        return JSON.parseObject(valueState, BlackList.class);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Boolean checkData(final Context ctx, final Long userId, Long time) {
        ChaincodeStub stub = ctx.getStub();

        String realKey = buildKey(userId);
        String valueState = stub.getStringState(realKey);
        if (valueState == null || valueState.length() <= 0) {
            BlackList data = init(userId, time);
            String json = JSON.toJSONString(data);
            try {
                stub.putStringState(realKey, json);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            LOGGER.info(String.format("init data:%s,json:%s", realKey, json));
            return Boolean.FALSE;
        }
        BlackList blackList = JSON.parseObject(valueState, BlackList.class);
        boolean go = SlideWindow.isGo(blackList, LIMIT, WINDOWS_TIME_LENGTH, time);
        blackList.setHit(!go);
        valueState = JSON.toJSONString(blackList);
        stub.putStringState(realKey, valueState);
        return Boolean.TRUE;

    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Boolean checkDataWithLimit(final Context ctx, final Long userId, Long time, Integer limit) {
        ChaincodeStub stub = ctx.getStub();
        String realKey = buildKey(userId);
        String valueState = stub.getStringState(realKey);
        if (valueState == null || valueState.length() <= 0) {
            BlackList data = init(userId, time);
            String json = JSON.toJSONString(data);
            try {
                stub.putStringState(realKey, json);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            LOGGER.info(String.format("init data:%s,json:%s", realKey, json));
            return Boolean.FALSE;
        }
        BlackList blackList = JSON.parseObject(valueState, BlackList.class);
        boolean go = SlideWindow.isGo(blackList, limit, WINDOWS_TIME_LENGTH, time);
        blackList.setHit(!go);
        valueState = JSON.toJSONString(blackList);
        stub.putStringState(realKey, valueState);
        LOGGER.info(String.format("insert data:%s,json:%s", realKey, valueState));
        return Boolean.TRUE;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Boolean unlock(final Context ctx, final Long userId, Long operatorId, Long time) {
        ChaincodeStub stub = ctx.getStub();
        String realKey = buildKey(userId);
        String valueState = stub.getStringState(realKey);
        if (valueState == null || valueState.length() <= 0) {
            return Boolean.TRUE;
        }
        BlackList blackList = JSON.parseObject(valueState, BlackList.class);
        if (blackList.getHit()) {
            blackList.setHit(Boolean.FALSE);
            blackList.setWindows(null);
            blackList.setOperatorId(operatorId);
            blackList.setOperatorAt(time);
            valueState = JSON.toJSONString(blackList);
            stub.putStringState(realKey, valueState);
        }
        return Boolean.TRUE;
    }

    public Boolean checkDataWithLimitTest(final Long userId, Long time, Integer limit) {
        String realKey = buildKey(userId);
        String valueState = MAP.get(realKey);
        if (valueState == null || valueState.length() <= 0) {
            BlackList data = init(userId, time);
            String json = JSON.toJSONString(data);
            try {
                MAP.put(realKey, json);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            LOGGER.info(String.format("init data:%s,json:%s", realKey, json));
            return false;
        }
        BlackList blackList = JSON.parseObject(valueState, BlackList.class);
        boolean go = SlideWindow.isGo(blackList, limit, WINDOWS_TIME_LENGTH, time);
        blackList.setHit(!go);
        valueState = JSON.toJSONString(blackList);
        MAP.put(realKey, valueState);
        LOGGER.info(String.format("insert data:%s,json:%s", realKey, valueState));
        return !blackList.getHit();
    }


    private BlackList init(Long userId, Long time) {
        BlackList bl = new BlackList();
        bl.setHit(false);
        bl.setUserId(userId);
        WindowData[] init = SlideWindow.init(WINDOWS_TIME_LENGTH, time);
        SlideWindow.addData(init, WINDOWS_TIME_LENGTH, time);
        bl.setWindows(init);
        bl.setDeleteFlag(false);
        bl.setOperatorId(userId);
        bl.setOperatorAt(time);

        return bl;
    }



}
