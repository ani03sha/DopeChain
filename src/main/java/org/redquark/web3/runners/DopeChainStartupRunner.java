package org.redquark.web3.runners;

import lombok.extern.slf4j.Slf4j;
import org.redquark.web3.entities.transactions.Transaction;
import org.redquark.web3.entities.transactions.TransactionOutput;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class DopeChainStartupRunner implements ApplicationRunner {

    // Global list of unspent transaction output mappings
    // transactionOutputId --> transactionOutput
    public static final Map<String, TransactionOutput> UTXOs = new HashMap<>();

    // Genesis transaction for this blockchain
    public static Transaction genesisTransaction;

    @Override
    public void run(ApplicationArguments args) {

    }
}
