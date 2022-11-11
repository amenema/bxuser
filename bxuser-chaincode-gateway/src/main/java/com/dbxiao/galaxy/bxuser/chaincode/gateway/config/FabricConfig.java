package com.dbxiao.galaxy.bxuser.chaincode.gateway.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * @author amen
 * @date 2022/11/4
 */
@Slf4j
@Configuration
@AllArgsConstructor
public class FabricConfig {

    @Autowired
    private HyperLedgerFabricProperties hyperLedgerFabricProperties;

    @Bean
    public Gateway gateway() {
        try {
            File file = ResourceUtils.getFile(hyperLedgerFabricProperties.getCertificatePath());
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader certificateReader = new BufferedReader(isr);

            X509Certificate certificate = Identities.readX509Certificate(certificateReader);
            File file1 = ResourceUtils.getFile(hyperLedgerFabricProperties.getPrivateKeyPath());
            FileInputStream fis1 = new FileInputStream(file1);
            InputStreamReader isr1 = new InputStreamReader(fis1);
            BufferedReader privateKeyReader = new BufferedReader(isr1);

            PrivateKey privateKey = Identities.readPrivateKey(privateKeyReader);

            Wallet wallet = Wallets.newInMemoryWallet();
            wallet.put("user1", Identities.newX509Identity("Org1MSP", certificate, privateKey));

            String connPath = ResourceUtils.getFile("classpath:connection-ca.json").getPath();

            Gateway.Builder builder = Gateway.createBuilder()
                    .identity(wallet, "user1")
                    .networkConfig(Paths.get(connPath));

            Gateway gateway = builder.connect();


            log.info("success connected fabric gateway {} ", gateway);

            return gateway;

        } catch (Exception e) {
            log.error("error connected fabric gateway {}", e);
        }
        return null;
    }

}
