package org.redquark.web3.services;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface SecurityKeyService {

    /**
     * @param publicKeyString - string representation of public key
     * @return @{@link PublicKey}
     */
    PublicKey getPublicKeyFromString(String publicKeyString);

    /**
     * @param privateKeyString - string representation of private key
     * @return @{@link PrivateKey}
     */
    PrivateKey getPrivateKeyFromString(String privateKeyString);
}
