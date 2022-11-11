package com.dbxiao.galaxy.bxuser.chaincode.gateway.service;

import com.alibaba.fastjson.JSON;
import com.dbxiao.galaxy.bxuser.chaincode.gateway.client.ChannelEnum;
import com.dbxiao.galaxy.bxuser.chaincode.gateway.client.FabricClient;
import com.dbxiao.galaxy.bxuser.chaincode.gateway.model.OperatorStashLog;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.sdk.Peer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author amen
 * @date 2022/10/28
 */
@Slf4j
@Service
public class LogStashService {

    private static final List<String> STOP_WORD = new ArrayList<>();

    static {
        STOP_WORD.add("body");
        STOP_WORD.add("result");
    }

    @Autowired
    private FabricClient fabricClient;


    public void stash(String index,String body, Boolean rs,Long operatorId,Long operatorAt){
        OperatorStashLog osl = build(index, body, rs, operatorId, operatorAt);

        try {
            Network network = fabricClient.network();
             network.getContract(ChannelEnum.LOG_CC.getChaincode())
                    .createTransaction("submitLog")
                    .setEndorsingPeers(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER))
                    ).submit(new String[]{JSON.toJSONString(osl)});
          } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static OperatorStashLog build(final String index, final String body,
                                         final Boolean rs,
                                         final Long operatorId, final Long operatorAt) {

        OperatorStashLog osl = new OperatorStashLog();
        osl.setKeys(new ArrayList<>(parse(body)));
        osl.setIndexName(index);
        osl.setLogDataMd5(encryption(body));
        osl.setRs(rs);
        osl.setOperatorAt(operatorAt);
        osl.setOperatorId(operatorId);
        return osl;
    }

    private static String encryption(String str) {
        StringBuilder md5 = new StringBuilder();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = messageDigest.digest();
            for (byte b : bytes) {
                md5.append(byteToHex(b));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5.toString();
    }

    private static String byteToHex(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() < 2) {
            hex = "0" + hex;
        }
        return hex;
    }

    private static Set<String> parse(String body) {
        Set<String> keys = new HashSet<>();
        try {
            IKSegmenter segmenter = new IKSegmenter(new StringReader(body), false);
            Lexeme next;
            while ((next = segmenter.next()) != null) {
                String lexemeText = next.getLexemeText();
                if (STOP_WORD.contains(lexemeText)) {
                    continue;
                }
                keys.add(lexemeText);
            }
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
        return keys;
    }
}
