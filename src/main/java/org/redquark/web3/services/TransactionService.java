package org.redquark.web3.services;

import org.redquark.web3.entities.transactions.Transaction;
import org.redquark.web3.entities.transactions.TransactionOutput;

import java.security.PrivateKey;
import java.util.Map;

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

    /**
     * @param transaction - for which signature needs to be verified
     * @return true, if the signature is correct, false otherwise
     */
    boolean verifySignature(Transaction transaction);

    /**
     * @param transaction - transaction to be processed
     * @param UTXOs       - unspent transaction outputs for the current transaction
     * @return true, if the transaction is processed successfully, false otherwise
     */
    boolean processTransaction(Transaction transaction, Map<String, TransactionOutput> UTXOs);
}
