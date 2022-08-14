package org.redquark.web3.runners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.redquark.web3.configs.DopeConfig;
import org.redquark.web3.entities.blocks.Block;
import org.redquark.web3.entities.transactions.Transaction;
import org.redquark.web3.entities.transactions.TransactionOutput;
import org.redquark.web3.entities.wallets.Wallet;
import org.redquark.web3.services.BlockService;
import org.redquark.web3.services.ChainValidatorService;
import org.redquark.web3.services.TransactionService;
import org.redquark.web3.services.WalletService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DopeChainStartupRunner implements ApplicationRunner {

    // Global list of all the blocks mined
    public static final List<Block> BLOCKCHAIN = new ArrayList<>();

    // Global list of unspent transaction output mappings
    // transactionOutputId --> transactionOutput
    public static final Map<String, TransactionOutput> UTXOs = new HashMap<>();

    // Genesis transaction for this blockchain
    public static Transaction genesisTransaction;

    private final WalletService walletService;
    private final TransactionService transactionService;
    private final BlockService blockService;
    private final ChainValidatorService chainValidatorService;
    private final DopeConfig dopeConfig;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Setting up *Bouncy Castle* as the security provider...");
        Security.addProvider(new BouncyCastleProvider());
        log.info("Creating two new wallets for genesis transaction...");
        KeyPair keyPairForDopeWallet = walletService.generateKeyPair();
        KeyPair keyPairForAnotherWallet = walletService.generateKeyPair();
        Wallet dopeWallet = Wallet
                .builder()
                .publicKey(keyPairForDopeWallet.getPublic())
                .privateKey(keyPairForDopeWallet.getPrivate())
                .build();
        log.info("Dope wallet is generated with address: {}", dopeWallet.getPublicKey());
        Wallet anotherWallet = Wallet
                .builder()
                .publicKey(keyPairForAnotherWallet.getPublic())
                .privateKey(keyPairForAnotherWallet.getPrivate())
                .build();
        log.info("Another wallet is generated with address: {}", anotherWallet.getPublicKey());
        genesisTransaction = Transaction
                .builder()
                .transactionId("0")
                .senderAddress(dopeWallet.getPublicKey())
                .recipientAddress(anotherWallet.getPublicKey())
                .amount(new BigDecimal("100.0"))
                .inputs(null)
                .build();
        genesisTransaction.setSignature(transactionService.generateSignature(genesisTransaction, dopeWallet.getPrivateKey()));
        TransactionOutput genesisTransactionOutput = TransactionOutput
                .builder()
                .id(genesisTransaction.getTransactionId())
                .amount(genesisTransaction.getAmount())
                .recipientAddress(genesisTransaction.getRecipientAddress())
                .build();
        genesisTransaction.setOutputs(Collections.singletonList(genesisTransactionOutput));
        log.info("Genesis transaction is created successfully");
        log.info("Updating the global list of UTXOs");
        UTXOs.put(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));
        log.info("Creating an mining *Genesis Block*...");
        Block genesis = Block
                .builder()
                .previousHash("0")
                .build();
        blockService.addTransaction(genesis, genesisTransaction);
        blockService.mineBlock(genesis, dopeConfig.getBlockConfig().getDifficulty());
        log.info("Block is mined successfully with hash: {}", genesis.getCurrentHash());
        log.info("Block is added to the blockchain");
        BLOCKCHAIN.add(genesis);
        log.info("Checking if the chain is valid...");
        if (chainValidatorService.isChainValid(BLOCKCHAIN)) {
            log.info("Chain is valid!");
            return;
        }
        log.info("Chain is not valid!");
    }
}
