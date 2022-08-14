package org.redquark.web3.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DopeConfig {

    @Bean
    @ConfigurationProperties("dsa-config")
    public DSAConfig getDSAConfig() {
        return new DSAConfig();
    }

    @Bean
    @ConfigurationProperties("block-config")
    public BlockConfig getBlockConfig() {
        return new BlockConfig();
    }

    @Bean
    @ConfigurationProperties("transaction-config")
    public TransactionConfig getTransactionConfig() {
        return new TransactionConfig();
    }
}
