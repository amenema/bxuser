package com.dbxiao.galaxy.bxuser.chaincode.logstash;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.hyperledger.fabric.Logger;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIteratorWithMetadata;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author amen
 * @date 2022/7/22
 */
@Default
@Contract(name = "logstashcc", info = @Info(title = "BXUser LogstashContract",
        description = "BXUser LogstashContract", version = "1.0"))
public class LogStashContract implements ContractInterface {

    private static final Logger LOGGER = Logger.getLogger("LogStashContract");

    private static final String INDEX_KEY = "log_split_index";
    private static final String LOG_KEY_FORMAT = "log_data_%s";

    public void index(){
    }

    private static final String buildDataKey(String key) {
        return String.format(LOG_KEY_FORMAT, key);

    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Boolean submitLog(final Context ctx, final String oslStr) {
        ChaincodeStub stub = ctx.getStub();
        OperatorStashLog osl = JSON.parseObject(oslStr, OperatorStashLog.class);
        String logDataKey = buildDataKey(osl.getLogDataMd5());
        stub.putState(logDataKey, oslStr.getBytes(StandardCharsets.UTF_8));
        return Boolean.TRUE;
    }


    @Transaction()
    public Page queryLogs(final Context ctx, final String query) {
        LogQuery logQuery = JSON.parseObject(query, LogQuery.class);
        String selector = buildQuery(logQuery);
        ChaincodeStub stub = ctx.getStub();
        QueryResultsIteratorWithMetadata<KeyValue> rs = null;
        try {
            rs = stub.getQueryResultWithPagination(selector, logQuery.getPageSize(), logQuery.getBookMark());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        if (rs == null) {
            return new Page();
        }
        org.hyperledger.fabric.protos.peer.ChaincodeShim.QueryResponseMetadata metadata = rs.getMetadata();
        Page page = new Page();
        page.setPageSize(logQuery.getPageSize());
        List<String> hashRs = new ArrayList<>();
        if (isNotEmpty(metadata)) {
            page.setBookMark(metadata.getBookmark());
        }
        Iterator<KeyValue> iterator = rs.iterator();
        if (isNotEmpty(iterator)) {
            while (iterator.hasNext()) {
                KeyValue next = iterator.next();
                String key = next.getKey();
                if ("logDataMd5".equals(key)) {
                    hashRs.add(next.getStringValue());
                }
            }
        }
        LOGGER.info("xxxx" + page.getBookMark());
        page.setDataMd5(hashRs);
        return page;
    }


    private static String buildQuery(LogQuery query) {
        JSONObject selector = new JSONObject();
        selector.put("indexName", query.getIndexName());
        if (isNotEmpty(query.getOperatorId())) {
            selector.put("operatorId", query.getOperatorId());
        }
        JSONObject operatorAtQ = new JSONObject();
        boolean operatorAtQFlag = false;
        if (isNotEmpty(query.getBeginAt())) {
            operatorAtQ.put("$gte", query.getEndAt());
            operatorAtQFlag = true;
        }
        if (isNotEmpty(query.getEndAt())) {
            operatorAtQ.put("$lte", query.getBeginAt());
            operatorAtQFlag = true;
        }
        if (operatorAtQFlag) {
            selector.put("operatorAt", operatorAtQ);
        }
        if (isNotEmpty(query.getMatchKeys()) && query.getMatchKeys().size() > 0) {
            JSONObject allQ = new JSONObject();
            allQ.put("$all", query.getMatchKeys());
            selector.put("keys", allQ);

        }
        JSONObject qObj = new JSONObject();
        qObj.put("selector", selector);
        return qObj.toJSONString();
    }


    private static Boolean isEmpty(Object data) {
        return data == null;
    }

    private static Boolean isNotEmpty(Object data) {
        return !isEmpty(data);
    }
}
