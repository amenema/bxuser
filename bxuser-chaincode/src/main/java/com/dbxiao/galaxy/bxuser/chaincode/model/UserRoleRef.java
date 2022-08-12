package com.dbxiao.galaxy.bxuser.chaincode.model;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.List;

/**
 * @author amen
 * @date 2022/7/29
 */
@DataType
public class UserRoleRef {

    @Property()
    private Long userId;
    @Property()
    private List<Long> refRoleIds;
    @Property()
    private Boolean deleteFlag;
    @Property()
    private Long operatorId;
    @Property()
    private Long operatorAt;


    public UserRoleRef(Long userId, List<Long> refRoleIds, Boolean deleteFlag, Long operatorId, Long operatorAt) {
        this.userId = userId;
        this.refRoleIds = refRoleIds;
        this.deleteFlag = deleteFlag;
        this.operatorId = operatorId;
        this.operatorAt = operatorAt;
    }

    public UserRoleRef() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Long> getRefRoleIds() {
        return refRoleIds;
    }

    public void setRefRoleIds(List<Long> refRoleIds) {
        this.refRoleIds = refRoleIds;
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
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        UserRoleRef other = (UserRoleRef) obj;

        return this.userId.equals(other.getUserId());
    }

    @Override
    public int hashCode() {
        return this.userId.hashCode();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + "useId:" +this.userId;
    }
}
