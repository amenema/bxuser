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
    private WindowData[] windows;


    @Property()
    private Boolean hit;

    @Property()
    private Boolean deleteFlag;
    @Property()
    private Long operatorId;
    @Property()
    private Long operatorAt;


    public BlackList(Long userId, WindowData[] windows, Boolean hit, Boolean deleteFlag, Long operatorId, Long operatorAt) {
        this.userId = userId;
        this.windows = windows;
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

    public WindowData[] getWindows() {
        return windows;
    }

    public void setWindows(WindowData[] windows) {
        this.windows = windows;
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
