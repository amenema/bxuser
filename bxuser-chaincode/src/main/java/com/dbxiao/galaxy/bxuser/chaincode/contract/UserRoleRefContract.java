package com.dbxiao.galaxy.bxuser.chaincode.contract;

import com.dbxiao.galaxy.bxuser.chaincode.model.UserRoleRef;
import com.dbxiao.galaxy.bxuser.chaincode.utils.JSON;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.ArrayList;

/**
 * @author amen
 * @date 2022/7/22
 */

@Contract(name = "userrolecc", info = @Info(title = "BXUser UserRoleRefContract",
        description = "BXUser UserRoleRefContract", version = "1.0.0"))
public class UserRoleRefContract implements ContractInterface {
    private static final String URF_KEY_FORMAT = "urf:%s";
    
    private static final String buildKey(Long key){
        return String.format(URF_KEY_FORMAT, key);
    }


    @Transaction()
    public UserRoleRef createURF(final Context ctx, final Long key, final UserRoleRef urf) {
        ChaincodeStub stub = ctx.getStub();

        String valueState = stub.getStringState(buildKey(key));
        if (!valueState.isEmpty()) {
            String errorMessage = String.format("urf %s already exists", key);
            throw new ChaincodeException(errorMessage, "key already exists");
        }

        valueState = JSON.toJSONString(urf);
        stub.putStringState(buildKey(key), valueState);

        return urf;
    }

    @Transaction()
    public UserRoleRef updateURF(final Context ctx, final Long key, UserRoleRef urf) {
        ChaincodeStub stub = ctx.getStub();

        String valueState = stub.getStringState(buildKey(key));

        if (valueState.isEmpty()) {
            String errorMessage = String.format("URF %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "no target urf key ");
        }

        valueState = JSON.toJSONString(urf);
        UserRoleRef old = JSON.parseObject(valueState, UserRoleRef.class);
        if (old.getDeleteFlag()) {
            String errorMessage = String.format("URF %s already deleted", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "URF already deleted ");
        }

        stub.putStringState(buildKey(key), valueState);
        return urf;
    }

    @Transaction()
    public UserRoleRef addRole(final Context ctx, final Long key, Long roleId) {
        ChaincodeStub stub = ctx.getStub();

        String valueState = stub.getStringState(buildKey(key));

        if (valueState.isEmpty()) {
            String errorMessage = String.format("URF %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "no target urf key ");
        }
        UserRoleRef urf = JSON.parseObject(valueState, UserRoleRef.class);
        if (urf.getDeleteFlag()) {
            String errorMessage = String.format("URF %s already deleted", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "URF already deleted ");
        }
        if (urf.getRefRoleIds() == null) {
            urf.setRefRoleIds(new ArrayList<Long>());
        }
        urf.getRefRoleIds().add(roleId);
        valueState = JSON.toJSONString(urf);
        stub.putStringState(buildKey(key), valueState);
        return urf;
    }

    @Transaction()
    public UserRoleRef cancelRole(final Context ctx, final Long key, Long roleId) {
        ChaincodeStub stub = ctx.getStub();

        String valueState = stub.getStringState(buildKey(key));

        if (valueState.isEmpty()) {
            String errorMessage = String.format("URF %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "no target urf key ");
        }
        UserRoleRef urf = JSON.parseObject(valueState, UserRoleRef.class);
        if (urf.getDeleteFlag()) {
            String errorMessage = String.format("URF %s already deleted", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "URF already deleted ");
        }
        if (urf.getRefRoleIds() == null) {
            return urf;
        }
        urf.getRefRoleIds().remove(roleId);
        valueState = JSON.toJSONString(urf);
        stub.putStringState(buildKey(key), valueState);
        return urf;
    }

    @Transaction()
    public UserRoleRef delURF(final Context ctx, final Long key) {
        ChaincodeStub stub = ctx.getStub();

        String valueState = stub.getStringState(buildKey(key));

        if (valueState.isEmpty()) {
            String errorMessage = String.format("URF %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "no target urf key ");
        }
        UserRoleRef urf = JSON.parseObject(valueState, UserRoleRef.class);
        if (urf.getRefRoleIds() == null) {
            return urf;
        }
        urf.setDeleteFlag(Boolean.TRUE);
        valueState = JSON.toJSONString(urf);
        stub.putStringState(buildKey(key), valueState);
        return urf;
    }

    @Transaction()
    public UserRoleRef queryURF(final Context ctx, Long key) {
        ChaincodeStub stub = ctx.getStub();
        String valueState = stub.getStringState(buildKey(key));

        if (valueState.isEmpty()) {
            String errorMessage = String.format("URF %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "no target urf key ");
        }
        UserRoleRef urf = JSON.parseObject(valueState, UserRoleRef.class);

        if (urf.getDeleteFlag()) {
            String errorMessage = String.format("URF %s already deleted", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "URF already deleted ");
        }
        return urf;
    }
}
