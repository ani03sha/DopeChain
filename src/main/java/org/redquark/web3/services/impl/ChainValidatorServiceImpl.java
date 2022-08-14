package org.redquark.web3.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redquark.web3.configs.DopeConfig;
import org.redquark.web3.entities.blocks.Block;
import org.redquark.web3.entities.transactions.Transaction;
import org.redquark.web3.entities.transactions.TransactionInput;
import org.redquark.web3.entities.transactions.TransactionOutput;
import org.redquark.web3.services.BlockService;
import org.redquark.web3.services.ChainValidatorService;
import org.redquark.web3.services.TransactionService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.redquark.web3.runners.DopeChainStartupRunner.genesisTransaction;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChainValidatorServiceImpl implements ChainValidatorService {

    private final BlockService blockService;
    private final TransactionService transactionService;
    private final DopeConfig dopeConfig;

    @Override
    public boolean isChainValid(List<Block> blockchain) {
        log.info("Validating blockchain...");
        // Current and previous blocks
        Block currentBlock;
        Block previousBlock;
        int difficulty = dopeConfig.getBlockConfig().getDifficulty();
        // Target hash with difficulty
        String targetHash = new String(new char[difficulty]).replace('\0', '0');
        // A temporary working list of unspent transactions at a given block state.
        Map<String, TransactionOutput> tempUTXOs = new HashMap<>();
        tempUTXOs.put(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));
        log.info("Traversing through blockchain to check hashes");
        for (int i = 1; i < blockchain.size(); i++) {
            previousBlock = blockchain.get(i - 1);
            currentBlock = blockchain.get(i);
            log.info("Comparing registered and calculated hash");
            String registeredHash = currentBlock.getCurrentHash();
            String calculatedHash = blockService.calculateBlockHash(currentBlock);
            if (!registeredHash.equals(calculatedHash)) {
                log.error("Block hashes are not equal");
                return false;
            }
            log.info("Block's registered and calculated hashes are equal. Proceeding further...");
            log.info("Comparing hashes of previous blocks...");
            registeredHash = currentBlock.getPreviousHash();
            calculatedHash = blockService.calculateBlockHash(previousBlock);
            if (!registeredHash.equals(calculatedHash)) {
                log.error("Previous block hashes are not equal.");
                return false;
            }
            log.info("Previous block hashes are equal. Proceeding further...");
            log.info("Checking if the hash is solved for the difficulty: {}", difficulty);
            if (!currentBlock.getCurrentHash().substring(0, difficulty).equals(targetHash)) {
                log.error("The block hasn't mined yet!");
                return false;
            }
            log.info("The block has been mined. Proceeding further...");
            log.info("Checking for block transactions...");
            TransactionOutput tempTransactionOutput;
            List<Transaction> currentBlockTransactions = currentBlock.getTransactions();
            for (Transaction currentTransaction : currentBlockTransactions) {
                if (!transactionService.verifySignature(currentTransaction)) {
                    log.info("Transaction signature is not verified for: {}", currentTransaction.getTransactionId());
                    return false;
                }
                for (TransactionInput input : currentTransaction.getInputs()) {
                    tempTransactionOutput = tempUTXOs.get(input.getTransactionOutputId());
                    if (tempTransactionOutput == null) {
                        log.error("References inputs are missing on transaction: {}", currentTransaction.getTransactionId());
                        return false;
                    }
                    if (!input.getUtxo().getAmount().equals(tempTransactionOutput.getAmount())) {
                        log.info("Referenced input transaction value is invalid for transaction: {}", currentTransaction.getTransactionId());
                        return false;
                    }
                    tempUTXOs.remove(input.getTransactionOutputId());
                }
                for (TransactionOutput output : currentTransaction.getOutputs()) {
                    tempUTXOs.put(output.getId(), output);
                }
                if (!currentTransaction.getOutputs().get(0).getRecipientAddress().equals(currentTransaction.getRecipientAddress())) {
                    log.error("Output recipient is not who it should be");
                    return false;
                }
                if (!currentTransaction.getOutputs().get(1).getRecipientAddress().equals(currentTransaction.getSenderAddress())) {
                    log.error("Output change is not for sender");
                    return false;
                }
            }
        }
        log.info("Blockchain is validated successfully");
        return true;
    }
}
