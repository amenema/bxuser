package com.dbxiao.galaxy.bxuser.chaincode.contract;

import com.dbxiao.galaxy.bxuser.chaincode.model.UserRoleRef;
import com.dbxiao.galaxy.bxuser.chaincode.utils.JSON;
import com.google.common.collect.Lists;
import org.hyperledger.fabric.Logger;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.ArrayList;

/**
 * @author amen
 * @date 2022/7/22
 */
@Default
@Contract(name = "userrolecc", info = @Info(title = "BXUser UserRoleRefContract",
        description = "BXUser UserRoleRefContract", version = "1.0"))
public class UserRoleRefContract implements ContractInterface {

    private static final Logger LOGGER = Logger.getLogger("UserRoleRefContract");

    private static final String URF_KEY_FORMAT = "urf_%s";

    private static final String buildKey(Long key) {
        return String.format(URF_KEY_FORMAT, key);
    }


    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Boolean createURF(final Context ctx, final UserRoleRef urf) {
        ChaincodeStub stub = ctx.getStub();
        String realKey = buildKey(urf.getUserId());
        String valueState = stub.getStringState(realKey);
        if (!isEmpty(valueState)) {
            String errorMessage = String.format("urf %s already exists", realKey);
            throw new ChaincodeException(errorMessage, "key already exists");
        }

        valueState = JSON.toJSONString(urf);
        stub.putStringState(realKey, valueState);

        return Boolean.TRUE;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Boolean updateURF(final Context ctx, final UserRoleRef urf) {
        ChaincodeStub stub = ctx.getStub();

        String realKey = buildKey(urf.getUserId());
        String valueState = stub.getStringState(realKey);

        if (isEmpty(valueState)) {
            String errorMessage = String.format("URF %s does not exist", realKey);
            LOGGER.error(errorMessage);
            throw new ChaincodeException(errorMessage, "no target urf key ");
        }

        valueState = JSON.toJSONString(urf);
        UserRoleRef old = JSON.parseObject(valueState, UserRoleRef.class);
        if (old.getDeleteFlag()) {
            String errorMessage = String.format("URF %s already deleted", realKey);
            LOGGER.error(errorMessage);
            throw new ChaincodeException(errorMessage, "URF already deleted ");
        }

        stub.putStringState(realKey, valueState);

        return Boolean.TRUE;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Boolean addRole(final Context ctx, final Long userId, final Long roleId,
                                         final Long operatorId, final Long time) {
        ChaincodeStub stub = ctx.getStub();

        String realKey = buildKey(userId);
        String valueState = stub.getStringState(realKey);
        LOGGER.info("before log");
        LOGGER.info("after log");
        if (valueState.isEmpty()) {
            LOGGER.info("empty data");
            UserRoleRef userRoleRef = new UserRoleRef();
            userRoleRef.setUserId(userId);
            userRoleRef.setRefRoleIds(Lists.newArrayList(roleId));
            userRoleRef.setDeleteFlag(Boolean.FALSE);
            userRoleRef.setOperatorId(operatorId);
            userRoleRef.setOperatorAt(time);
            stub.putStringState(realKey, JSON.toJSONString(userRoleRef));

            LOGGER.info("return empty data");
            return Boolean.TRUE;
        }
        UserRoleRef urf = JSON.parseObject(valueState, UserRoleRef.class);
        if (urf.getDeleteFlag()) {
            String errorMessage = String.format("URF %s already deleted", realKey);
            return Boolean.TRUE;
        }
        if (urf.getRefRoleIds() == null) {
            urf.setRefRoleIds(new ArrayList<Long>());
        }
        if (!urf.getRefRoleIds().contains(roleId)) {
            urf.getRefRoleIds().add(roleId);
        }
        urf.setOperatorAt(time);
        urf.setOperatorId(operatorId);
        valueState = JSON.toJSONString(urf);
        LOGGER.info("brefore existed data");
        stub.putStringState(realKey, valueState);
        LOGGER.info("after existed data");
        return Boolean.TRUE;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Boolean cancelRole(final Context ctx, final Long userId, final Long roleId,
                                            final Long operatorId, final Long time) {
        ChaincodeStub stub = ctx.getStub();

        String realKey = buildKey(userId);
        String valueState = stub.getStringState(realKey);

        if (isEmpty(valueState)) {
            String errorMessage = String.format("URF %s does not exist", realKey);
            LOGGER.error(errorMessage);

            return Boolean.TRUE;
        }
        UserRoleRef urf = JSON.parseObject(valueState, UserRoleRef.class);
        if (urf.getDeleteFlag()) {
            String errorMessage = String.format("URF %s already deleted", realKey);
            LOGGER.error(errorMessage);

            return Boolean.TRUE;
        }
        if (urf.getRefRoleIds() == null) {

            return Boolean.TRUE;
        }
        urf.getRefRoleIds().remove(roleId);
        urf.setOperatorAt(time);
        urf.setOperatorId(operatorId);
        valueState = JSON.toJSONString(urf);
        stub.putStringState(realKey, valueState);

        return Boolean.TRUE;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Boolean delURF(final Context ctx, final Long userId,
                                        final Long operatorId, final Long time) {
        ChaincodeStub stub = ctx.getStub();

        String realKey = buildKey(userId);
        String valueState = stub.getStringState(realKey);

        if (isEmpty(valueState)) {

            return Boolean.TRUE;
        }
        UserRoleRef urf = JSON.parseObject(valueState, UserRoleRef.class);
        urf.setDeleteFlag(Boolean.TRUE);
        urf.setOperatorAt(time);
        urf.setOperatorId(operatorId);
        valueState = JSON.toJSONString(urf);
        stub.putStringState(realKey, valueState);

        return Boolean.TRUE;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public UserRoleRef queryURF(final Context ctx, final Long userId) {
        ChaincodeStub stub = ctx.getStub();
        String realKey = buildKey(userId);
        String valueState = stub.getStringState(realKey);
        if (isEmpty(valueState)) {
            String errorMessage = String.format("URF %s does not exist", realKey);
            LOGGER.error(errorMessage);
            return new UserRoleRef();
        }
        UserRoleRef urf = JSON.parseObject(valueState, UserRoleRef.class);

        if (urf.getDeleteFlag()) {
            String errorMessage = String.format("URF %s already deleted", realKey);
            LOGGER.error(errorMessage);
            return new UserRoleRef();
        }
        return urf;
    }

    private Boolean isEmpty(String data) {
        return data == null || data.length() <= 0;
    }


}
