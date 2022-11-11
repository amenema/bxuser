package com.dbxiao.galaxy.bxuser.chaincode.gateway.client;

import com.dbxiao.galaxy.bxuser.chaincode.gateway.api.query.CommonBody;
import com.dbxiao.galaxy.bxuser.chaincode.gateway.aspect.LogStash;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.sdk.Peer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

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


    public Network network(){
        return gateway.getNetwork(CHANNEL_NAME);
    }

    @LogStash("")
    public String query(CommonBody params) {
        params.expandArgs();
        try {
            byte[] bytes = gateway.getNetwork(CHANNEL_NAME).getContract(params.getChannelEnum().getChaincode())
                    .evaluateTransaction(params.getMethod(), params.getArgs().toArray(new String[]{}));
            String res = StringUtils.newStringUtf8(bytes);
            return res;
        } catch (ContractException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @LogStash("")
    public String submit(CommonBody params) {
        params.expandArgs();
        try {
            Network network = gateway.getNetwork(CHANNEL_NAME);
            byte[] bytes = network.getContract(params.getChannelEnum().getChaincode())
                    .createTransaction(params.getMethod())
                    .setEndorsingPeers(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER))
                    ).submit(params.getArgs().toArray(new String[]{}));
            String res = StringUtils.newStringUtf8(bytes);
            return res;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);

        }
    }



}
