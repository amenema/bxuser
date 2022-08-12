package com.dbxiao.galaxy.bxuser.chaincode.roleprivilege;

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
        description = "BXUser RolePrivilegeRefContract", version = "1.0.0"))
public class RolePrivilegeRefContract implements ContractInterface {

    private static final String RPR_KEY_FORMAT = "rpr:%s";

    private static final String buildKey(Long key){
        return String.format(RPR_KEY_FORMAT, key);
    }

    @Transaction()
    public RolePrivilegeRef createRPR(final Context ctx, final Long key, final RolePrivilegeRef rpr) {
        ChaincodeStub stub = ctx.getStub();

        String valueState = stub.getStringState(buildKey(key));
        if (!valueState.isEmpty()) {
            String errorMessage = String.format("RPR %s already exists", key);
            throw new ChaincodeException(errorMessage, "key already exists");
        }

        valueState = JSON.toJSONString(rpr);
        stub.putStringState(buildKey(key), valueState);

        return rpr;
    }

    @Transaction()
    public RolePrivilegeRef updateRPR(final Context ctx, final Long key, RolePrivilegeRef rpr) {
        ChaincodeStub stub = ctx.getStub();

        String valueState = stub.getStringState(buildKey(key));

        if (valueState.isEmpty()) {
            String errorMessage = String.format("RPR %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "no target RPR key ");
        }

        valueState = JSON.toJSONString(rpr);
        RolePrivilegeRef old = JSON.parseObject(valueState, RolePrivilegeRef.class);
        if (old.getDeleteFlag()) {
            String errorMessage = String.format("RPR %s already deleted", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "RPR already deleted ");
        }

        stub.putStringState(buildKey(key), valueState);
        return rpr;
    }

    @Transaction()
    public RolePrivilegeRef addPrivilege(final Context ctx, final Long key, Long roleId) {
        ChaincodeStub stub = ctx.getStub();

        String valueState = stub.getStringState(buildKey(key));

        if (valueState.isEmpty()) {
            String errorMessage = String.format("RPR %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "no target RPR key ");
        }
        RolePrivilegeRef rpr = JSON.parseObject(valueState, RolePrivilegeRef.class);
        if (rpr.getDeleteFlag()) {
            String errorMessage = String.format("RPR %s already deleted", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "RPR already deleted ");
        }
        if (rpr.getPrivilegeId() == null) {
            rpr.setPrivilegeId(new ArrayList<Long>());
        }
        rpr.getPrivilegeId().add(roleId);
        valueState = JSON.toJSONString(rpr);
        stub.putStringState(buildKey(key), valueState);
        return rpr;
    }

    @Transaction()
    public RolePrivilegeRef cancelPrivilege(final Context ctx, final Long key, Long roleId) {
        ChaincodeStub stub = ctx.getStub();

        String valueState = stub.getStringState(buildKey(key));

        if (valueState.isEmpty()) {
            String errorMessage = String.format("RPR %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "no target RPR key ");
        }
        RolePrivilegeRef rpr = JSON.parseObject(valueState, RolePrivilegeRef.class);
        if (rpr.getDeleteFlag()) {
            String errorMessage = String.format("RPR %s already deleted", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "RPR already deleted ");
        }
        if (rpr.getPrivilegeId() == null) {
            return rpr;
        }
        rpr.getPrivilegeId().remove(roleId);
        valueState = JSON.toJSONString(rpr);
        stub.putStringState(buildKey(key), valueState);
        return rpr;
    }

    @Transaction()
    public RolePrivilegeRef delRPR(final Context ctx, final Long key) {
        ChaincodeStub stub = ctx.getStub();

        String valueState = stub.getStringState(buildKey(key));

        if (valueState.isEmpty()) {
            String errorMessage = String.format("RPR %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "no target RPR key ");
        }
        RolePrivilegeRef rpr = JSON.parseObject(valueState, RolePrivilegeRef.class);
        if (rpr.getPrivilegeId() == null) {
            return rpr;
        }
        rpr.setDeleteFlag(Boolean.TRUE);
        valueState = JSON.toJSONString(rpr);
        stub.putStringState(buildKey(key), valueState);
        return rpr;
    }

    @Transaction()
    public RolePrivilegeRef queryRPR(final Context ctx, Long key) {
        ChaincodeStub stub = ctx.getStub();
        String valueState = stub.getStringState(buildKey(key));

        if (valueState.isEmpty()) {
            String errorMessage = String.format("RPR %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "no target RPR key ");
        }
        RolePrivilegeRef rpr = JSON.parseObject(valueState, RolePrivilegeRef.class);
        if (rpr.getDeleteFlag()) {
            String errorMessage = String.format("RPR %s already deleted", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "RPR already deleted ");
        }
        return rpr;
    }
}
