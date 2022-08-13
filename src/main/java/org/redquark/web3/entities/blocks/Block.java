package org.redquark.web3.entities.blocks;

import lombok.Builder;
import lombok.Data;
import org.redquark.web3.entities.transactions.Transaction;

import java.util.List;

/**
 * This class represents the fundamental entity of a blockchain, i.e, a block
 */
@Data
@Builder
public class Block {

    // Hash of the previous block
    private String previousHash;
    // Hash of the current block
    private String currentHash;
    // List of all the transactions to be included in this block
    private List<Transaction> transactions;
    // Merkle root for this block
    private String merkleRoot;
    // Number of milliseconds since January 1st, 1970 (Unix epoch)
    private long timestamp;
    // An encrypted number that a miner must solve to validate the block
    private int nonce;
}
