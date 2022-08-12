package com.dbxiao.galaxy.bxuser.chaincode.roleprivilege;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.List;

/**
 * @author amen
 * @date 2022/7/29
 */
@DataType
public class RolePrivilegeRef {

    @Property()
    private Long roleId;
    @Property()
    private List<Long> privilegeId;

    @Property()
    private Boolean deleteFlag;
    @Property()
    private Long operatorId;
    @Property()
    private Long operatorAt;


    public RolePrivilegeRef(Long roleId, List<Long> privilegeId, Boolean deleteFlag, Long operatorId, Long operatorAt) {
        this.roleId = roleId;
        this.privilegeId = privilegeId;
        this.deleteFlag = deleteFlag;
        this.operatorId = operatorId;
        this.operatorAt = operatorAt;
    }

    public RolePrivilegeRef() {
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public List<Long> getPrivilegeId() {
        return privilegeId;
    }

    public void setPrivilegeId(List<Long> privilegeId) {
        privilegeId = privilegeId;
    }

    public Boolean getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Long getOperatorAt() {
        return operatorAt;
    }

    public void setOperatorAt(Long operatorAt) {
        this.operatorAt = operatorAt;
    }
    @Override
    public int hashCode() {
        return this.roleId.hashCode();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + "useId:" +this.roleId;
    }
}
