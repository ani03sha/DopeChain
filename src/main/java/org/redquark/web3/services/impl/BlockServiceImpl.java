package org.redquark.web3.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redquark.web3.entities.blocks.Block;
import org.redquark.web3.entities.transactions.Transaction;
import org.redquark.web3.services.BlockService;
import org.redquark.web3.services.HashService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockServiceImpl implements BlockService {

    private final HashService hashService;

    @Override
    public String calculateBlockHash(Block block) {
        // Extract all the information from the block which we don't
        // want to get tampered
        String previousHash = block.getPreviousHash();
        long timestamp = block.getTimestamp();
        int nonce = block.getNonce();
        String merkleRoot = block.getMerkleRoot();
        String data = previousHash
                .concat(String.valueOf(timestamp))
                .concat(String.valueOf(nonce))
                .concat(merkleRoot);
        return hashService.createSHA256Hash(data);
    }

    @Override
    public void mineBlock(Block block, int difficulty) {
        log.info("Getting the merkle root for this block...");
        String merkleRoot = hashService.getMerkleRoot(block.getTransactions());
        log.info("The calculated merkle root is: {}", merkleRoot);
        // Create a string with difficulty * "0"
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!block.getCurrentHash().substring(0, difficulty).equals(target)) {
            int nonce = block.getNonce();
            block.setNonce(nonce + 1);
            block.setCurrentHash(calculateBlockHash(block));
        }
        log.info("Block mined with hash: {}!", block.getCurrentHash());
    }

    @Override
    public void addTransaction(Block block, Transaction transaction) {
        log.info("Checking if the transaction is valid");
        if (transaction == null) {
            log.error("Transaction in invalid. Discarding...");
            return;
        }
        // If the block is genesis, we will ignore it
        if (block.getPreviousHash().equals("0")) {
            log.error("This is a genesis block. We cannot add transaction in it. Discarding...");
            return;
        }
        log.info("Adding transaction to the block...");
        block.getTransactions().add(transaction);
        log.info("Transaction is successfully added to the block!");
    }
}
