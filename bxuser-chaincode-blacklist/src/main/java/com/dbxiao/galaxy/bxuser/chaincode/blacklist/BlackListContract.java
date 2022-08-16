package com.dbxiao.galaxy.bxuser.chaincode.blacklist;

import org.hyperledger.fabric.Logger;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeStub;

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
    private static final Integer LIMIT = 100;

    private static final Logger LOGGER = Logger.getLogger("BlackListContract");
    private static final String buildKey(Long key) {
        return String.format(BLACK_LIST_KEY_FORMAT, key);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public BlackList Query(final Context ctx, final  Long userId){
        ChaincodeStub stub = ctx.getStub();

        String realKey = buildKey(userId);
        String valueState = stub.getStringState(realKey);
        if(valueState == null || valueState.length() <=0){
            return new BlackList();
        }
        return JSON.parseObject(valueState, BlackList.class);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public BlackList CheckData(final Context ctx, final Long userId, Long time) {
        ChaincodeStub stub = ctx.getStub();

        String realKey = buildKey(userId);
        String valueState = stub.getStringState(realKey);
        if (valueState == null || valueState.length() <=0 ) {
            BlackList data = init(userId, time);
            String json = JSON.toJSONString(data);
            try {
                stub.putStringState(realKey, json);
            }catch (Exception e){
                LOGGER.error(e.getMessage());
            }
            LOGGER.info(String.format("init data:%s,json:%s", realKey,json));
            return data;
        }
        BlackList blackList = JSON.parseObject(valueState, BlackList.class);
        incr(blackList, time);
        valueState = JSON.toJSONString(blackList);
        stub.putStringState(realKey, valueState);
        return blackList;
    }

    private void incr(BlackList bl, Long time){
        long diff = time - bl.getJoinAt();
        if(diff >= WINDOWS_TIME_LENGTH){
            bl.setJoinAt(time);
            bl.setCount(1);
            bl.setHit(Boolean.FALSE);
            return;
        }
        bl.setCount(bl.getCount() + 1);
        bl.setHit(bl.getCount() >= LIMIT);
    }
    private BlackList init(Long userId, Long time) {
        BlackList blackList = new BlackList();
        blackList.setCount(1);
        blackList.setDeleteFlag(Boolean.FALSE);
        blackList.setUserId(userId);
        blackList.setJoinAt(time);
        blackList.setOperatorAt(time);
        blackList.setOperatorId(userId);
        blackList.setHit(Boolean.FALSE);
        return blackList;
    }
}
