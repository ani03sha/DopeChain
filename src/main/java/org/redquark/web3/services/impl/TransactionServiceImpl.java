package org.redquark.web3.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redquark.web3.entities.transactions.Transaction;
import org.redquark.web3.services.HashService;
import org.redquark.web3.services.TransactionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.security.PublicKey;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    // This sequence will maintain the rough count of transactions generated
    private static int SEQUENCE = 0;

    private final HashService hashService;

    @Override
    public String calculateTransactionHash(Transaction transaction) {
        SEQUENCE++;
        PublicKey senderAddress = transaction.getSenderAddress();
        PublicKey recipientAddress = transaction.getRecipientAddress();
        BigDecimal amount = transaction.getAmount();
        String senderAddressString = hashService.getStringFromKey(senderAddress);
        String recipientAddressString = hashService.getStringFromKey(recipientAddress);
        // Create input for hash by appending everything which we don't
        // want to change
        String inputForHash = senderAddressString
                .concat(recipientAddressString)
                .concat(String.valueOf(amount))
                .concat(String.valueOf(SEQUENCE));
        return hashService.createSHA256Hash(inputForHash);
    }

    @Override
    public byte[] generateSignature(Transaction transaction, PrivateKey privateKey) {
        PublicKey senderAddress = transaction.getSenderAddress();
        PublicKey recipientAddress = transaction.getRecipientAddress();
        String inputForSignature = hashService.getStringFromKey(senderAddress)
                .concat(hashService.getStringFromKey(recipientAddress))
                .concat(String.valueOf(transaction.getAmount()));
        return hashService.applyECDSASignature(privateKey, inputForSignature);
    }
}
