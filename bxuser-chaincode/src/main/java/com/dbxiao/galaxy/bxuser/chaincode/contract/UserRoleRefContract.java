package com.dbxiao.galaxy.bxuser.chaincode.contract;

import com.dbxiao.galaxy.bxuser.chaincode.model.ResponseData;
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
import org.hyperledger.fabric.shim.Chaincode;
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
    public ResponseData<UserRoleRef> createURF(final Context ctx, final UserRoleRef urf) {
        ChaincodeStub stub = ctx.getStub();
        String realKey = buildKey(urf.getUserId());
        String valueState = stub.getStringState(realKey);
        if (!isEmpty(valueState)) {
            String errorMessage = String.format("urf %s already exists", realKey);
            throw new ChaincodeException(errorMessage, "key already exists");
        }

        valueState = JSON.toJSONString(urf);
        stub.putStringState(realKey, valueState);
        String logData = submitLog(stub, "createUrf", valueState, urf.getOperatorId(), urf.getOperatorAt());
        ResponseData<UserRoleRef> rd = new ResponseData<>();
        rd.setData(urf);
        rd.setLogDta(logData);
        return rd;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public ResponseData<UserRoleRef> updateURF(final Context ctx, final UserRoleRef urf) {
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
        String logData = submitLog(stub, "updateUrf", valueState, urf.getOperatorId(), urf.getOperatorAt());
        ResponseData<UserRoleRef> rd = new ResponseData<>();
        rd.setData(urf);
        rd.setLogDta(logData);
        return rd;
    }

    @Transaction()
    public ResponseData<Boolean> addRole(final Context ctx, final Long userId, final Long roleId,
                                         final Long operatorId, final Long time) {
        ChaincodeStub stub = ctx.getStub();

        String realKey = buildKey(userId);
        String valueState = stub.getStringState(realKey);

        String logData = submitLog(stub, "addRole", String.format("%s,%s", userId.toString() + roleId.toString()), operatorId, time);
        if (valueState.isEmpty()) {
            UserRoleRef userRoleRef = new UserRoleRef();
            userRoleRef.setUserId(userId);
            userRoleRef.setRefRoleIds(Lists.newArrayList(roleId));
            userRoleRef.setDeleteFlag(Boolean.FALSE);
            userRoleRef.setOperatorId(operatorId);
            userRoleRef.setOperatorAt(time);
            stub.putStringState(realKey, JSON.toJSONString(userRoleRef));
            ResponseData<Boolean> rd = new ResponseData<>();
            rd.setData(Boolean.TRUE);
            rd.setLogDta(logData);
            return rd;
        }
        UserRoleRef urf = JSON.parseObject(valueState, UserRoleRef.class);
        if (urf.getDeleteFlag()) {
            String errorMessage = String.format("URF %s already deleted", realKey);
            LOGGER.error(errorMessage);
            ResponseData<Boolean> rd = new ResponseData<>();
            rd.setData(Boolean.TRUE);
            rd.setLogDta(logData);
            return rd;
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
        stub.putStringState(realKey, valueState);
        ResponseData<Boolean> rd = new ResponseData<>();
        rd.setData(Boolean.TRUE);
        rd.setLogDta(logData);
        return rd;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public ResponseData<Boolean> cancelRole(final Context ctx, final Long userId, final Long roleId,
                                            final Long operatorId, final Long time) {
        ChaincodeStub stub = ctx.getStub();
        String logData = submitLog(stub, "cancelRole", String.format("%s,%s", userId.toString() + roleId.toString()), operatorId, time);

        String realKey = buildKey(userId);
        String valueState = stub.getStringState(realKey);

        if (isEmpty(valueState)) {
            String errorMessage = String.format("URF %s does not exist", realKey);
            LOGGER.error(errorMessage);
            ResponseData<Boolean> rd = new ResponseData<>();
            rd.setData(Boolean.TRUE);
            rd.setLogDta(logData);
            return rd;
        }
        UserRoleRef urf = JSON.parseObject(valueState, UserRoleRef.class);
        if (urf.getDeleteFlag()) {
            String errorMessage = String.format("URF %s already deleted", realKey);
            LOGGER.error(errorMessage);
            ResponseData<Boolean> rd = new ResponseData<>();
            rd.setData(Boolean.TRUE);
            rd.setLogDta(logData);
            return rd;
        }
        if (urf.getRefRoleIds() == null) {
            ResponseData<Boolean> rd = new ResponseData<>();
            rd.setData(Boolean.TRUE);
            rd.setLogDta(logData);
            return rd;
        }
        urf.getRefRoleIds().remove(roleId);
        urf.setOperatorAt(time);
        urf.setOperatorId(operatorId);
        valueState = JSON.toJSONString(urf);
        stub.putStringState(realKey, valueState);
        ResponseData<Boolean> rd = new ResponseData<>();
        rd.setData(Boolean.TRUE);
        rd.setLogDta(logData);
        return rd;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public ResponseData<Boolean> delURF(final Context ctx, final Long userId,
                                        final Long operatorId, final Long time) {
        ChaincodeStub stub = ctx.getStub();
        String logData = submitLog(stub, "cancelRole", userId.toString(), operatorId, time);

        String realKey = buildKey(userId);
        String valueState = stub.getStringState(realKey);

        if (isEmpty(valueState)) {
            ResponseData<Boolean> rd = new ResponseData<>();
            rd.setData(Boolean.TRUE);
            rd.setLogDta(logData);
            return rd;
        }
        UserRoleRef urf = JSON.parseObject(valueState, UserRoleRef.class);
        urf.setDeleteFlag(Boolean.TRUE);
        urf.setOperatorAt(time);
        urf.setOperatorId(operatorId);
        valueState = JSON.toJSONString(urf);
        stub.putStringState(realKey, valueState);
        ResponseData<Boolean> rd = new ResponseData<>();
        rd.setData(Boolean.TRUE);
        rd.setLogDta(logData);
        return rd;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public ResponseData<UserRoleRef> queryURF(final Context ctx, final Long userId) {
        ChaincodeStub stub = ctx.getStub();
        String realKey = buildKey(userId);
        String valueState = stub.getStringState(realKey);

        if (isEmpty(valueState)) {
            String errorMessage = String.format("URF %s does not exist", realKey);
            LOGGER.error(errorMessage);
            return ResponseData.success(new UserRoleRef());
        }
        UserRoleRef urf = JSON.parseObject(valueState, UserRoleRef.class);

        if (urf.getDeleteFlag()) {
            String errorMessage = String.format("URF %s already deleted", realKey);
            LOGGER.error(errorMessage);
            return ResponseData.success(new UserRoleRef());
        }
        return ResponseData.success(urf);
    }

    private Boolean isEmpty(String data) {
        return data == null || data.length() <= 0;
    }

    public String submitLog(ChaincodeStub stub, final String methodName,
                            final String body,
                            final Long operatorId, final Long operatorAt) {
        Chaincode.Response logstash = stub.invokeChaincodeWithStringArgs("logstashcc",
                "submitLog", "UserRoleContract",
                methodName, body, operatorId.toString(), operatorAt.toString());
        return logstash.getStringPayload();
    }
}
