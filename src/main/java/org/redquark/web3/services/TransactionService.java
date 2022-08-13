package org.redquark.web3.services;

import org.redquark.web3.entities.transactions.Transaction;

import java.security.PrivateKey;

public interface TransactionService {

    /**
     * @param transaction - a transaction object
     * @return hash from the fields of the passed transaction object
     */
    String calculateTransactionHash(Transaction transaction);

    /**
     * @param transaction - transaction for which signature needs to be created
     * @param privateKey  - private key of the sender to generate the signature
     * @return ECDSA signature
     */
    byte[] generateSignature(Transaction transaction, PrivateKey privateKey);
}
