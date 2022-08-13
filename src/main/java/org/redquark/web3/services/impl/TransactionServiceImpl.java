package org.redquark.web3.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redquark.web3.entities.transactions.Transaction;
import org.redquark.web3.entities.transactions.TransactionInput;
import org.redquark.web3.entities.transactions.TransactionOutput;
import org.redquark.web3.services.HashService;
import org.redquark.web3.services.TransactionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.redquark.web3.constants.DopeConstants.MINIMUM_TRANSACTION;

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
        // Create input for hash by appending everything which we don't
        // want to change
        String data = constructDataToBeHashed(transaction)
                .concat(String.valueOf(SEQUENCE));
        return hashService.createSHA256Hash(data);
    }

    @Override
    public byte[] generateSignature(Transaction transaction, PrivateKey privateKey) {
        String data = constructDataToBeHashed(transaction);
        return hashService.applyECDSASignature(privateKey, data);
    }

    @Override
    public boolean verifySignature(Transaction transaction) {
        byte[] signature = transaction.getSignature();
        PublicKey senderAddress = transaction.getSenderAddress();
        String data = constructDataToBeHashed(transaction);
        return hashService.verifyECDSASignature(senderAddress, data, signature);
    }

    @Override
    public boolean processTransaction(Transaction transaction, Map<String, TransactionOutput> UTXOs) {
        log.info("Verifying the transaction...");
        if (!verifySignature(transaction)) {
            log.error("Transaction verification failed. Discarding the transaction.");
            return false;
        }
        log.info("Transaction was verified successfully!");
        log.info("Gathering transaction inputs and making sure they are unspent...");
        for (TransactionInput input : transaction.getInputs()) {
            input.setUtxo(UTXOs.get(input.getTransactionOutputId()));
        }
        log.info("All unspent transaction outputs are gathered at stored!");
        log.info("Checking if the transaction is valid...");
        if (calculateTransactionInputValues(transaction).compareTo(MINIMUM_TRANSACTION) < 0) {
            log.error("Not enough transaction inputs");
            return false;
        }
        log.info("Transaction is valid. Proceeding further...");
        log.info("Generating transaction outputs...");
        BigDecimal leftOver = calculateTransactionInputValues(transaction).subtract(transaction.getAmount());
        transaction.setTransactionId(calculateTransactionHash(transaction));
        log.info("Sending amount to the recipient...");
        TransactionOutput toRecipient = TransactionOutput
                .builder()
                .id(transaction.getTransactionId())
                .amount(transaction.getAmount())
                .recipientAddress(transaction.getRecipientAddress())
                .build();
        log.info("Amount is sent to the recipient!");
        log.info("Getting the change back...");
        TransactionOutput backToSender = TransactionOutput
                .builder()
                .id(transaction.getTransactionId())
                .amount(leftOver)
                .recipientAddress(transaction.getSenderAddress())
                .build();
        log.info("Got change back!");
        List<TransactionOutput> outputs = Arrays.asList(toRecipient, backToSender);
        transaction.setOutputs(outputs);
        log.info("Adding transaction outputs to the global UTXO...");
        for (TransactionOutput output : transaction.getOutputs()) {
            UTXOs.put(transaction.getTransactionId(), output);
        }
        log.info("Added transaction outputs to the global UTXOs!");
        log.info("Removing transaction inputs from UTXOs as spent...");
        for (TransactionInput input : transaction.getInputs()) {
            if (input.getUtxo() != null) {
                UTXOs.remove(input.getUtxo().getId());
            }
        }
        log.info("Removed transaction inputs from UTXOs!");
        return true;
    }

    private String constructDataToBeHashed(Transaction transaction) {
        PublicKey senderAddress = transaction.getSenderAddress();
        PublicKey recipientAddress = transaction.getRecipientAddress();
        BigDecimal amount = transaction.getAmount();
        return hashService.getStringFromKey(senderAddress)
                .concat(hashService.getStringFromKey(recipientAddress))
                .concat(String.valueOf(amount));
    }

    private BigDecimal calculateTransactionInputValues(Transaction transaction) {
        BigDecimal total = new BigDecimal("");
        List<TransactionInput> inputs = transaction.getInputs();
        for (TransactionInput input : inputs) {
            if (input.getUtxo() != null) {
                total = total.add(input.getUtxo().getAmount());
            }
        }
        return total;
    }

    private BigDecimal calculateTransactionOutputValues(Transaction transaction) {
        BigDecimal total = new BigDecimal("");
        List<TransactionOutput> outputs = transaction.getOutputs();
        for (TransactionOutput output : outputs) {
            total = total.add(output.getAmount());
        }
        return total;
    }
}
