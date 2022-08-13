package org.redquark.web3.entities.transactions;

import java.math.BigDecimal;
import java.security.PrivateKey;

public class TransactionOutput {

    // ID of this transaction
    private String id;
    // Private key of the new owner of the funds
    private PrivateKey recipientAddress;
    // Amount (cryptocurrency) that they own
    private BigDecimal amount;
}
