package com.dbxiao.galaxy.bxuser.chaincode.contract;

import com.dbxiao.galaxy.bxuser.chaincode.model.ResponseData;
import com.dbxiao.galaxy.bxuser.chaincode.model.RolePrivilegeRef;
import com.dbxiao.galaxy.bxuser.chaincode.utils.JSON;
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
@Contract(name = "roleprivilegecc", info = @Info(title = "BXUser RolePrivilegeRefContract",
        description = "BXUser RolePrivilegeRefContract", version = "1.0"))
public class RolePrivilegeRefContract implements ContractInterface {
    private static final Logger LOGGER = Logger.getLogger("RolePrivilegeRefContract");

    private static final String RPR_KEY_FORMAT = "rprc_%s";

    private static final String buildKey(Long key) {
        return String.format(RPR_KEY_FORMAT, key);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public ResponseData<RolePrivilegeRef> createRPR(final Context ctx, final RolePrivilegeRef rpr) {
        ChaincodeStub stub = ctx.getStub();

        String realKey = buildKey(rpr.getRoleId());
        String valueState = stub.getStringState(realKey);
        if (!isEmpty(valueState)) {
            String errorMessage = String.format("RPR %s already exists", realKey);
            throw new ChaincodeException(errorMessage, "key already exists");
        }

        valueState = JSON.toJSONString(rpr);
        stub.putStringState(realKey, valueState);
        String logData = submitLog(stub, "createRPR", valueState, rpr.getOperatorId(), rpr.getOperatorAt());
        return ResponseData.success(rpr, logData);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public ResponseData<RolePrivilegeRef> updateRpr(final Context ctx, RolePrivilegeRef rpr) {
        ChaincodeStub stub = ctx.getStub();

        String realKey = buildKey(rpr.getRoleId());
        String valueState = stub.getStringState(realKey);

        if (isEmpty(valueState)) {
            String errorMessage = String.format("RPR %s does not exist", realKey);
            LOGGER.error(errorMessage);
            throw new ChaincodeException(errorMessage, "no target RPR key ");
        }

        valueState = JSON.toJSONString(rpr);
        RolePrivilegeRef old = JSON.parseObject(valueState, RolePrivilegeRef.class);
        if (old.getDeleteFlag()) {
            String errorMessage = String.format("RPR %s already deleted", realKey);
            LOGGER.error(errorMessage);
            throw new ChaincodeException(errorMessage, "RPR already deleted ");
        }

        stub.putStringState(realKey, valueState);
        String logData = submitLog(stub, "updateRpr", valueState, rpr.getOperatorId(), rpr.getOperatorAt());
        return ResponseData.success(rpr, logData);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public ResponseData<Boolean> addPrivilege(final Context ctx, final Long roleId, final Long privilegeId,
                                              final Long operatorId, final Long time) {
        ChaincodeStub stub = ctx.getStub();
        String logData = submitLog(stub, "addPrivilege", String.format("%s,%s", roleId.toString() + privilegeId.toString()), operatorId, time);

        String realKey = buildKey(roleId);
        String valueState = stub.getStringState(realKey);

        if (valueState.isEmpty()) {
            RolePrivilegeRef rolePrivilegeRef = new RolePrivilegeRef();
            rolePrivilegeRef.setRoleId(roleId);
            ArrayList<Long> priviletes = new ArrayList<Long>();
            priviletes.add(privilegeId);
            rolePrivilegeRef.setDeleteFlag(Boolean.FALSE);
            rolePrivilegeRef.setOperatorId(operatorId);
            rolePrivilegeRef.setOperatorAt(time);
            stub.putStringState(realKey, JSON.toJSONString(rolePrivilegeRef));
            return ResponseData.success(Boolean.TRUE, logData);
        }
        RolePrivilegeRef rpr =  JSON.parseObject(valueState, RolePrivilegeRef.class);
        if (rpr.getDeleteFlag()) {
            String errorMessage = String.format("URF %s already deleted", realKey);
            LOGGER.error(errorMessage);
            return ResponseData.success(Boolean.TRUE, logData);
        }
        if (rpr.getPrivilegeIds() == null) {
            rpr.setPrivilegeId(new ArrayList<Long>());
        }
        if(!rpr.getPrivilegeIds().contains(roleId)){
            rpr.getPrivilegeIds().add(roleId);
        }
        rpr.setOperatorAt(time);
        rpr.setOperatorId(operatorId);
        valueState = JSON.toJSONString(rpr);
        stub.putStringState(realKey, valueState);
        return ResponseData.success(Boolean.TRUE, logData);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public ResponseData<Boolean> cancelPrivilege(final Context ctx, final Long roleId, final Long privilegeId,
                                            final Long operatorId, final Long time) {
        ChaincodeStub stub = ctx.getStub();
        String logData = submitLog(stub, "cancelPrivilege", String.format("%s,%s", roleId.toString() + privilegeId.toString()), operatorId, time);

        String realKey = buildKey(roleId);
        String valueState = stub.getStringState(realKey);

        if (isEmpty(valueState)) {
            String errorMessage = String.format("RPR %s does not exist", realKey);
            LOGGER.error(errorMessage);
            return ResponseData.success(Boolean.TRUE,logData);
        }
        RolePrivilegeRef rpr = JSON.parseObject(valueState, RolePrivilegeRef.class);
        if (rpr.getDeleteFlag()) {
            String errorMessage = String.format("RPR %s already deleted", realKey);
            LOGGER.error(errorMessage);
            return ResponseData.success(Boolean.TRUE,logData);
        }
        if (rpr.getPrivilegeIds() == null) {
            return ResponseData.success(Boolean.TRUE,logData);
        }
        rpr.getPrivilegeIds().remove(privilegeId);
        rpr.setOperatorId(operatorId);
        rpr.setOperatorAt(time);
        valueState = JSON.toJSONString(rpr);
        stub.putStringState(realKey, valueState);
        return ResponseData.success(Boolean.TRUE,logData);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public ResponseData<Boolean> delRPR(final Context ctx, final Long roleId,
                          final Long operatorId, final Long time) {
        ChaincodeStub stub = ctx.getStub();
        String logData = submitLog(stub, "delRPR",  roleId.toString(), operatorId, time);

        String realKey = buildKey(roleId);
        String valueState = stub.getStringState(realKey);

        if (isEmpty(valueState)) {
            return ResponseData.success(Boolean.TRUE,logData);
        }
        RolePrivilegeRef rpr = JSON.parseObject(valueState, RolePrivilegeRef.class);
        rpr.setDeleteFlag(Boolean.TRUE);
        rpr.setOperatorAt(time);
        rpr.setOperatorId(operatorId);
        valueState = JSON.toJSONString(rpr);
        stub.putStringState(realKey, valueState);
        return ResponseData.success(Boolean.TRUE,logData);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public ResponseData<RolePrivilegeRef> QueryRPR(final Context ctx, final Long roleId) {
        ChaincodeStub stub = ctx.getStub();
        String realKey = buildKey(roleId);
        String valueState = stub.getStringState(realKey);

        if (isEmpty(valueState)) {
            String errorMessage = String.format("RPR %s does not exist", realKey);
            LOGGER.error(errorMessage);
            return ResponseData.success(new RolePrivilegeRef());
        }
        RolePrivilegeRef rpr = JSON.parseObject(valueState, RolePrivilegeRef.class);
        if (rpr.getDeleteFlag()) {
            String errorMessage = String.format("RPR %s already deleted", realKey);
            LOGGER.error(errorMessage);
            return ResponseData.success(new RolePrivilegeRef());
        }
        return ResponseData.success(rpr);
    }

    private Boolean isEmpty(String data) {
        return data == null || data.length() <= 0;
    }

    public String submitLog(ChaincodeStub stub, final String methodName,
                            final String body,
                            final Long operatorId, final Long operatorAt) {
        Chaincode.Response logstash = stub.invokeChaincodeWithStringArgs("logstashcc", "submitLog", "RolePrivilegeRefContract",
                methodName, body, operatorId.toString(), operatorAt.toString());
        return logstash.getStringPayload();
    }
}
