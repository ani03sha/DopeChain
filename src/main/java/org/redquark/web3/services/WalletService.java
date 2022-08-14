package org.redquark.web3.services;

import org.redquark.web3.entities.transactions.Transaction;
import org.redquark.web3.entities.transactions.TransactionOutput;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.PrivateKey;
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
     * @param address - public key of the wallet in question
     * @param UTXOs   - global list of UTXOs
     * @return balance in the wallet
     */
    BigDecimal getBalance(PublicKey address, Map<String, TransactionOutput> UTXOs);

    /**
     * @param sender     - public key of the sender
     * @param recipient  - public key/address of the recipient
     * @param signingKey - private key of the sender
     * @param amount     - amount to be sent to the recipient
     * @param UTXOs      - global list of unspent transaction outputs
     * @return a transaction object
     */
    Transaction sendFunds(PublicKey sender, PublicKey recipient, PrivateKey signingKey, BigDecimal amount, Map<String, TransactionOutput> UTXOs);
}
