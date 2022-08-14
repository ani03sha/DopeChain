package org.redquark.web3.requests;

import lombok.Data;

@Data
public class TransactionRequest {

    private String senderAddress;
    private String recipientAddress;
    private String senderSigningKey;
    private String amount;
}
