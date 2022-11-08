package com.dbxiao.galaxy.bxuser.chaincode.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author amen
 * @date 2022/11/4
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "fabric")
public class HyperLedgerFabricProperties {
    String networkConnectionConfigPath;
    String certificatePath;
    String privateKeyPath;
}
