package com.dbxiao.galaxy.bxuser.chaincode.gateway.client;

import com.alibaba.fastjson.JSON;
import com.dbxiao.galaxy.bxuser.chaincode.gateway.client.model.ResponseData;
import com.dbxiao.galaxy.bxuser.chaincode.gateway.service.LogDataParseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Gateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author amen
 * @date 2022/11/4
 */
@Slf4j
@Service
public class FabricClient {

    private static final String CHANNEL_NAME = "devchannel";

    @Autowired
    private Gateway gateway;

    @Autowired
    private LogDataParseService logDataParseService;

    public String query(ChannelEnum channelEnum, String method, String... args) {
        try {
            byte[] bytes = gateway.getNetwork(CHANNEL_NAME).getContract(channelEnum.getName()).evaluateTransaction(method, args);
            String res = StringUtils.newStringUtf8(bytes);
            ResponseData responseData = JSON.parseObject(res, ResponseData.class);
            String logDta = responseData.getLogDta();
            logDataParseService.create(logDta);
            return responseData.getData();
        } catch (ContractException e) {
            throw new RuntimeException(e);
        }
    }

}
