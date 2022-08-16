package com.dbxiao.galaxy.bxuser.chaincode.blacklist;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

/**
 * @author amen
 * @date 2022/7/29
 */
@DataType
public class BlackList {



    @Property()
    private Long userId;
    @Property()
    private Long joinAt;
    @Property()
    private Integer count;


    @Property()
    private Boolean hit;

    @Property()
    private Boolean deleteFlag;
    @Property()
    private Long operatorId;
    @Property()
    private Long operatorAt;



    public BlackList(Long userId, Long joinAt, Integer count, Boolean hit, Boolean deleteFlag, Long operatorId, Long operatorAt) {
        this.userId = userId;
        this.joinAt = joinAt;
        this.count = count;
        this.hit = hit;
        this.deleteFlag = deleteFlag;
        this.operatorId = operatorId;
        this.operatorAt = operatorAt;
    }

    public BlackList() {
    }


    public Long getUserId() {
        return userId;
    }

    public Boolean getHit() {
        return hit;
    }

    public void setHit(Boolean hit) {
        this.hit = hit;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getJoinAt() {
        return joinAt;
    }

    public void setJoinAt(Long joinAt) {
        this.joinAt = joinAt;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
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
}
