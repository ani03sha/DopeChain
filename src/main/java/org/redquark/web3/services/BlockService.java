package org.redquark.web3.services;

import org.redquark.web3.entities.blocks.Block;
import org.redquark.web3.entities.transactions.Transaction;

public interface BlockService {

    /**
     * @param block - block whose hash is needed
     * @return calculated hash
     */
    String calculateBlockHash(Block block);

    /**
     * @param block      - block instance to be mined
     * @param difficulty - difficulty value a miner needs to solve for
     */
    void mineBlock(Block block, int difficulty);

    /**
     * @param block       - block in which transaction needs to be added
     * @param transaction - transaction to be added
     */
    void addTransaction(Block block, Transaction transaction);
}
