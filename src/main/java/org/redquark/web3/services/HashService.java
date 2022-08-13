package org.redquark.web3.services;

import org.redquark.web3.entities.transactions.Transaction;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

public interface HashService {

    /**
     * @param input - input string for which hash needs to be calculated
     * @return SHA256 hash corresponding to the input
     */
    String createSHA256Hash(String input);

    /**
     * @param key - public/private key
     * @return encoded value of the key
     */
    String getStringFromKey(Key key);

    /**
     * @param privateKey - private key of the sender
     * @param input      - combination of fields which we don't want to get tampered
     * @return Elliptic curve digital signature
     */
    byte[] applyECDSASignature(PrivateKey privateKey, String input);

    /**
     * @param publicKey - sender's public key
     * @param data      - data on which we applied signature
     * @param signature - ECDSA signature
     * @return true, if signing was done correctly, false otherwise
     */
    boolean verifyECDSASignature(PublicKey publicKey, String data, byte[] signature);

    /**
     * @param transactions - list of transactions
     * @return merkle root for this block
     */
    String getMerkleRoot(List<Transaction> transactions);
}
