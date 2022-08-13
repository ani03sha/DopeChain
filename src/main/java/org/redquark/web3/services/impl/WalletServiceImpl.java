package org.redquark.web3.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redquark.web3.entities.transactions.Transaction;
import org.redquark.web3.entities.transactions.TransactionInput;
import org.redquark.web3.entities.transactions.TransactionOutput;
import org.redquark.web3.entities.wallets.Wallet;
import org.redquark.web3.services.TransactionService;
import org.redquark.web3.services.WalletService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final TransactionService transactionService;

    @Override
    public KeyPair generateKeyPair() {
        try {
            log.info("Generating keypair using Elliptic Curve cryptography with Bouncy Castle as provider...");
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("prime192v1");
            // Initialize key generator and generate a KeyPair
            keyPairGenerator.initialize(ecGenParameterSpec, secureRandom);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            log.error("Could not generate key pair due to: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public BigDecimal getBalance(Wallet wallet, Map<String, TransactionOutput> UTXOs) {
        BigDecimal total = new BigDecimal("");
        for (Map.Entry<String, TransactionOutput> entry : UTXOs.entrySet()) {
            TransactionOutput UTXO = entry.getValue();
            // Check if the coin belongs to the one who is claiming
            if (UTXO.getRecipientAddress().equals(wallet.getPublicKey())) {
                UTXOs.put(UTXO.getId(), UTXO);
                total = total.add(UTXO.getAmount());
            }
        }
        return total;
    }

    @Override
    public Transaction sendFunds(Wallet wallet, PublicKey recipient, BigDecimal amount, Map<String, TransactionOutput> UTXOs) {
        log.info("Checking if there are enough funds to send...");
        if (getBalance(wallet, UTXOs).compareTo(amount) < 0) {
            log.error("Not enough funds to send. Discarding operation!");
            return null;
        }
        log.info("There are enough funds to send. Proceeding further...");
        // Create list of inputs
        List<TransactionInput> inputs = new ArrayList<>();
        BigDecimal total = new BigDecimal("");
        for (Map.Entry<String, TransactionOutput> entry : UTXOs.entrySet()) {
            TransactionOutput UTXO = entry.getValue();
            total = total.add(amount);
            TransactionInput input = TransactionInput
                    .builder()
                    .transactionOutputId(UTXO.getId())
                    .build();
            inputs.add(input);
            if (total.compareTo(amount) > 0) {
                break;
            }
        }
        log.info("Creating a new transaction...");
        Transaction newTransaction = Transaction
                .builder()
                .senderAddress(wallet.getPublicKey())
                .recipientAddress(recipient)
                .amount(amount)
                .inputs(inputs)
                .build();
        log.info("A new transaction is created!");
        log.info("Signing the transaction...");
        newTransaction.setSignature(transactionService.generateSignature(newTransaction, wallet.getPrivateKey()));
        log.info("Transaction is signed!");
        log.info("Removing inputs from the global UTXOs...");
        for (TransactionInput input : newTransaction.getInputs()) {
            UTXOs.remove(input.getTransactionOutputId());
        }
        log.info("Removed all the inputs from the global UTXOs!");
        return newTransaction;
    }
}
