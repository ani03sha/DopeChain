package org.redquark.web3.entities.transactions;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.List;

/**
 * Transactions are cryptographically signed data messages that contain a set of instructions.
 * These instructions can interpret to sending crypto from one wallet account to another or
 * interacting with a smart contract deployed on the blockchain.
 */
@Data
@Builder
public class Transaction {

    // Unique id of the transaction which will be generated
    // by calculating the hash. The hash will be calculated
    // by taking everything into account which we don't want
    // to tamper with
    private String transactionId;
    // Public key of the sender (Wallet address of the sender)
    private PublicKey senderAddress;
    // Public key of the recipient (Waller address of the recipient)
    private PublicKey recipientAddress;
    // Amount to be sent
    private BigDecimal amount;
    // Signature to prevent anyone else to use our funds. This proves
    // that the owner is the one who is sending the funds.
    private byte[] signature;
    // Inputs from the previous transactions. This is used to verify
    // that the sender has funds to send, i.e., prevents double spending
    private List<TransactionInput> inputs;
    // Outputs which show the amount relevant address received in the
    // next transaction.
    private List<TransactionOutput> outputs;
}
