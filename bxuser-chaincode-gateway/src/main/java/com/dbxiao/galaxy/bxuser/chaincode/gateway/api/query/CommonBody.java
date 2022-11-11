package com.dbxiao.galaxy.bxuser.chaincode.gateway.api.query;

import com.dbxiao.galaxy.bxuser.chaincode.gateway.client.ChannelEnum;
import lombok.Data;

import java.util.List;

/**
 * @author amen
 * @date 2022/11/9
 */
@Data
public class CommonBody {
    private ChannelEnum channelEnum;
    private String method;
    private List<String> args;
    private Long operatorId;
    private Long operatorAt;

    public void expandArgs(){
        args.add(operatorId.toString());
        args.add(operatorAt.toString());
    }
}
