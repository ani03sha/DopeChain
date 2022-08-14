package org.redquark.web3.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redquark.web3.entities.transactions.Transaction;
import org.redquark.web3.entities.wallets.Wallet;
import org.redquark.web3.requests.TransactionRequest;
import org.redquark.web3.services.SecurityKeyService;
import org.redquark.web3.services.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.redquark.web3.runner.DopeChainStartupRunner.UTXOs;

@RestController
@RequestMapping(value = "/api/v1/dopechain/wallet")
@RequiredArgsConstructor
@Slf4j
public class WalletController {

    private final WalletService walletService;
    private final SecurityKeyService securityKeyService;

    @PostMapping(value = "/create")
    public ResponseEntity<Wallet> createWallet() {
        log.info("Got a request to create a new wallet");
        KeyPair keyPair = walletService.generateKeyPair();
        if (keyPair != null) {
            Wallet wallet = Wallet
                    .builder()
                    .publicKey(keyPair.getPublic())
                    .privateKey(keyPair.getPrivate())
                    .build();
            log.info("Wallet was created successfully!");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(wallet);
        }
        log.error("Could not create wallet!");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
    }

    @GetMapping(value = "/balance")
    public ResponseEntity<String> getWalletBalance(String address) {
        log.info("Extracting public key of the wallet...");
        PublicKey publicKey = securityKeyService.getPublicKeyFromString(address);
        if (publicKey != null) {
            log.info("Extracted public key!");
            log.info("Getting balance of the wallet");
            BigDecimal balance = walletService.getBalance(publicKey, UTXOs);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(String.valueOf(balance));
        } else {
            log.error("Could not extract public key of the wallet");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not get balance");
        }
    }

    @PostMapping(value = "/transact")
    public ResponseEntity<Transaction> sendFunds(@RequestBody TransactionRequest request) {
        log.info("Received request for the transaction");
        PublicKey senderAddress = securityKeyService.getPublicKeyFromString(request.getSenderAddress());
        PublicKey recipientAddress = securityKeyService.getPublicKeyFromString(request.getRecipientAddress());
        PrivateKey senderSigningKey = securityKeyService.getPrivateKeyFromString(request.getSenderSigningKey());
        BigDecimal amount = new BigDecimal(request.getAmount());
        log.info("Sending funds to: {} from: {}", recipientAddress, senderAddress);
        Transaction transaction = walletService.sendFunds(
                senderAddress,
                recipientAddress,
                senderSigningKey,
                amount,
                UTXOs
        );
        if (transaction != null) {
            log.info("Funds have been sent to:{} from: {}", recipientAddress, senderAddress);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(transaction);
        }
        log.info("Could not send funds!");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
    }
}
