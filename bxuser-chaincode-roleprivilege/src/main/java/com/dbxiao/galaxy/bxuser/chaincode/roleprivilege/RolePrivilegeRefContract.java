package com.dbxiao.galaxy.bxuser.chaincode.roleprivilege;

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
@Contract(name = "roleprivilegecc", info = @Info(title = "BXUser RolePrivilegeRefContract",
        description = "BXUser RolePrivilegeRefContract", version = "1.0"))
public class RolePrivilegeRefContract implements ContractInterface {
    private static final Logger LOGGER = Logger.getLogger("RolePrivilegeRefContract");

    private static final String RPR_KEY_FORMAT = "rprc_%s";

    private static final String buildKey(Long key) {
        return String.format(RPR_KEY_FORMAT, key);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public RolePrivilegeRef CreateRPR(final Context ctx, final RolePrivilegeRef rpr) {
        ChaincodeStub stub = ctx.getStub();

        String realKey = buildKey(rpr.getRoleId());
        String valueState = stub.getStringState(realKey);
        if (!isEmpty(valueState)) {
            String errorMessage = String.format("RPR %s already exists", realKey);
            throw new ChaincodeException(errorMessage, "key already exists");
        }

        valueState = JSON.toJSONString(rpr);
        stub.putStringState(realKey, valueState);

        return rpr;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public RolePrivilegeRef UpdateRPR(final Context ctx, RolePrivilegeRef rpr) {
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
        return rpr;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Boolean AddPrivilege(final Context ctx, final Long roleId, final Long privilegeId,
                           final Long operatorId,final Long time) {
        ChaincodeStub stub = ctx.getStub();

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
            return Boolean.TRUE;
        }
        RolePrivilegeRef rpr =  JSON.parseObject(valueState, RolePrivilegeRef.class);
        if (rpr.getDeleteFlag()) {
            String errorMessage = String.format("URF %s already deleted", realKey);
            LOGGER.error(errorMessage);
            return Boolean.TRUE;
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
        return Boolean.TRUE;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Boolean CancelPrivilege(final Context ctx, final Long roleId, final Long privilegeId,
                                            final Long operatorId, final Long time) {
        ChaincodeStub stub = ctx.getStub();

        String realKey = buildKey(roleId);
        String valueState = stub.getStringState(realKey);

        if (isEmpty(valueState)) {
            String errorMessage = String.format("RPR %s does not exist", realKey);
            LOGGER.error(errorMessage);
            return Boolean.TRUE;
        }
        RolePrivilegeRef rpr = JSON.parseObject(valueState, RolePrivilegeRef.class);
        if (rpr.getDeleteFlag()) {
            String errorMessage = String.format("RPR %s already deleted", realKey);
            LOGGER.error(errorMessage);
            return Boolean.TRUE;
        }
        if (rpr.getPrivilegeIds() == null) {
            return Boolean.TRUE;
        }
        rpr.getPrivilegeIds().remove(privilegeId);
        rpr.setOperatorId(operatorId);
        rpr.setOperatorAt(time);
        valueState = JSON.toJSONString(rpr);
        stub.putStringState(realKey, valueState);
        return Boolean.TRUE;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Boolean DelRPR(final Context ctx, final Long roleId,
                          final Long operatorId, final Long time) {
        ChaincodeStub stub = ctx.getStub();

        String realKey = buildKey(roleId);
        String valueState = stub.getStringState(realKey);

        if (isEmpty(valueState)) {
            return Boolean.TRUE;
        }
        RolePrivilegeRef rpr = JSON.parseObject(valueState, RolePrivilegeRef.class);
        rpr.setDeleteFlag(Boolean.TRUE);
        rpr.setOperatorAt(time);
        rpr.setOperatorId(operatorId);
        valueState = JSON.toJSONString(rpr);
        stub.putStringState(realKey, valueState);
        return Boolean.TRUE;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public RolePrivilegeRef QueryRPR(final Context ctx, final Long roleId) {
        ChaincodeStub stub = ctx.getStub();
        String realKey = buildKey(roleId);
        String valueState = stub.getStringState(realKey);

        if (isEmpty(valueState)) {
            String errorMessage = String.format("RPR %s does not exist", realKey);
            LOGGER.error(errorMessage);
            return new RolePrivilegeRef();
        }
        RolePrivilegeRef rpr = JSON.parseObject(valueState, RolePrivilegeRef.class);
        if (rpr.getDeleteFlag()) {
            String errorMessage = String.format("RPR %s already deleted", realKey);
            LOGGER.error(errorMessage);
            return new RolePrivilegeRef();
        }
        return rpr;
    }

    private Boolean isEmpty(String data) {
        return data == null || data.length() <= 0;
    }
}
