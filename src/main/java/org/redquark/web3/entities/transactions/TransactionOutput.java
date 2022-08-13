package org.redquark.web3.entities.transactions;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@Builder
public class TransactionOutput {

    // ID of this transaction
    private String id;
    // Public key of the new owner of the funds
    private PublicKey recipientAddress;
    // Amount (cryptocurrency) that they own
    private BigDecimal amount;
}
