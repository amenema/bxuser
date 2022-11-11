package com.dbxiao.galaxy.bxuser.chaincode.gateway.api;

import com.dbxiao.galaxy.bxuser.chaincode.gateway.api.query.CommonBody;
import com.dbxiao.galaxy.bxuser.chaincode.gateway.client.FabricClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author amen
 * @date 2022/11/9
 */
@RestController
@RequestMapping("/fabric")
public class QueryController {

    @Autowired
    private FabricClient fabricClient;

    @PostMapping("/query")
    public String query(@RequestBody CommonBody params) {
        String rs = fabricClient.query(params);
        return rs;
    }

    @PostMapping("/submit")
    public String submit(@RequestBody CommonBody params) {
        String rs = fabricClient.submit(params);
        return rs;
    }
}
