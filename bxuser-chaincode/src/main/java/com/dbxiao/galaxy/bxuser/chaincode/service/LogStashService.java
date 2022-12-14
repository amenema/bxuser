package com.dbxiao.galaxy.bxuser.chaincode.service;

import com.dbxiao.galaxy.bxuser.chaincode.model.OperatorStashLog;
import org.hyperledger.fabric.Logger;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author amen
 * @date 2022/10/28
 */
public class LogStashService {

    private static final List<String> STOP_WORD = new ArrayList<>();

    static {
        STOP_WORD.add("body");
        STOP_WORD.add("result");
    }

    private static final Logger LOGGER = Logger.getLogger("LogStashService");

    public static OperatorStashLog build(final String index, final List<String> keys,
                                         final Boolean rs,
                                         final String indexMd5,
                                         final Long operatorId, final Long operatorAt) {

        OperatorStashLog osl = new OperatorStashLog();
        osl.setKeys(keys);
        osl.setIndexName(index);
        osl.setLogDataMd5(indexMd5);
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
            LOGGER.error(e.getMessage());
        }
        return keys;
    }
}
