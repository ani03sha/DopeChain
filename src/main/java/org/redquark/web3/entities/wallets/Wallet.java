package org.redquark.web3.entities.wallets;

import lombok.Builder;
import lombok.Data;
import org.redquark.web3.entities.transactions.TransactionOutput;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

@Data
@Builder
public class Wallet {

    // Public key of the wallet - also known as the wallet address
    private PublicKey publicKey;
    // Private key of the wallet - will be used to sign transactions
    private PrivateKey privateKey;
    // List of UTXOs for this wallet
    private Map<String, TransactionOutput> UTXOs;
}
