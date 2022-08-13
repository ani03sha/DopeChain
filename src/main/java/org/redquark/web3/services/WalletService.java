package org.redquark.web3.services;

import org.redquark.web3.entities.transactions.Transaction;
import org.redquark.web3.entities.transactions.TransactionOutput;
import org.redquark.web3.entities.wallets.Wallet;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Map;

public interface WalletService {

    /**
     * Generates key pair (public and private) for the wallet
     *
     * @return symmetric keypair
     */
    KeyPair generateKeyPair();

    /**
     * @param wallet - wallet in question
     * @param UTXOs  - global list of UTXOs
     * @return balance in the wallet
     */
    BigDecimal getBalance(Wallet wallet, Map<String, TransactionOutput> UTXOs);

    /**
     * @param wallet    - wallet in question
     * @param recipient - public key/address of the recipient
     * @param amount    - amount to be sent to the recipient
     * @param UTXOs     - global list of unspent transaction outputs
     * @return a transaction object
     */
    Transaction sendFunds(Wallet wallet, PublicKey recipient, BigDecimal amount, Map<String, TransactionOutput> UTXOs);
}
