package com.dbxiao.galaxy.bxuser.chaincode.contract;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbxiao.galaxy.bxuser.chaincode.dto.Page;
import com.dbxiao.galaxy.bxuser.chaincode.model.OperatorStashLog;
import com.dbxiao.galaxy.bxuser.chaincode.query.LogQuery;
import com.dbxiao.galaxy.bxuser.chaincode.service.LogStashService;
import com.dbxiao.galaxy.bxuser.chaincode.utils.JSON;
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

    private static final String buildDataKey(String key) {
        return String.format(LOG_KEY_FORMAT, key);

    }


    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public OperatorStashLog submitLog(final Context ctx, final String serviceName, final String methodName,
                                      final String body,
                                      final Long operatorId, final Long operatorAt) {
        OperatorStashLog osl = LogStashService.build(serviceName, methodName, body, operatorId, operatorAt);
        ChaincodeStub stub = ctx.getStub();
        String logDataKey = buildDataKey(osl.getLogDataMd5());
        stub.putState(logDataKey, JSON.toJSONString(osl).getBytes(StandardCharsets.UTF_8));
        return osl;
    }


    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Page queryLogs(final Context ctx, final LogQuery query) {
        ChaincodeStub stub = ctx.getStub();

        JSONObject selector = new JSONObject();
        JSONObject and = new JSONObject();
        JSONArray arrays = new JSONArray();
        for (String mk : query.getMatchKeys()) {
            JSONObject ele = new JSONObject();
            JSONObject eleK = new JSONObject();
            eleK.put("$elemMatch", mk);
            ele.put("keysMd5", eleK);
            arrays.add(ele);
        }
        and.put("$and", arrays);
        selector.put("selector", and);
        QueryResultsIteratorWithMetadata<KeyValue> rs = stub.getQueryResultWithPagination(selector.toJSONString(), query.getPageSize(), query.getBookMark());
        Page page = new Page();
        page.setPageSize(query.getPageSize());
        List<String> hashRs = new ArrayList<>();
        page.setBookMark(rs.getMetadata().getBookmark());
        for (KeyValue kv : rs) {
            hashRs.add(kv.getKey());
        }
        page.setDataMd5(hashRs);
        return page;
    }


    private Boolean isEmpty(String data) {
        return data == null || data.length() <= 0;
    }
}
