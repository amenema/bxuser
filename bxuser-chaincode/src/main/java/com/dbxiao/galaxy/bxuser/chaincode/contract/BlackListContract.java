package com.dbxiao.galaxy.bxuser.chaincode.contract;

import com.dbxiao.galaxy.bxuser.chaincode.model.BlackList;
import com.dbxiao.galaxy.bxuser.chaincode.utils.JSON;
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
        description = "BXUser BlackListContract", version = "1.0.0"))
public class BlackListContract implements ContractInterface {
    private static final String BLACK_LIST_KEY_FORMAT = "blacklist:%s";

    private static final Long WINDOWS_TIME_LENGTH = 5 * 60 * 10000L;
    private static final Integer LIMIT = 100;

    private static final String buildKey(String key) {
        return String.format(BLACK_LIST_KEY_FORMAT, key);
    }

    @Transaction()
    public BlackList check(final Context ctx, final String userId) {
        ChaincodeStub stub = ctx.getStub();

        String realKey = buildKey(userId);
        String valueState = stub.getStringState(realKey);
        BlackList blackList = null;
        if (valueState.isEmpty()) {
            return init(userId);
        }

        blackList = JSON.parseObject(valueState, BlackList.class);
        incr(blackList);
        valueState = JSON.toJSONString(blackList);
        stub.putStringState(realKey, valueState);
        return blackList;
    }

    private void incr(BlackList bl){
        long now = System.currentTimeMillis();
        long diff = now - bl.getJoinAt();
        if(diff >= WINDOWS_TIME_LENGTH){
            bl.setJoinAt(now);
            bl.setCount(1);
            bl.setHit(Boolean.FALSE);
            return;
        }
        bl.setCount(bl.getCount() + 1);
        bl.setHit(bl.getCount() >= LIMIT);
    }
    private BlackList init(String userId) {
        BlackList blackList = new BlackList();
        blackList.setCount(1);
        blackList.setDeleteFlag(Boolean.FALSE);
        blackList.setUserId(Long.valueOf(userId));
        blackList.setJoinAt(System.currentTimeMillis());
        blackList.setOperatorAt(System.currentTimeMillis());
        blackList.setOperatorId(Long.valueOf(userId));
        blackList.setHit(Boolean.FALSE);
        return blackList;
    }
}
