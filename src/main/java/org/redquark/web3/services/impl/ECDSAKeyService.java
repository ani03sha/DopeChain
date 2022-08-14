package org.redquark.web3.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.redquark.web3.services.SecurityKeyService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static org.redquark.web3.constants.DopeConstants.ECDSA;

@Service
@Slf4j
public class ECDSAKeyService implements SecurityKeyService {

    @Override
    public PublicKey getPublicKeyFromString(String publicKeyString) {
        try {
            byte[] publicBytes = Base64.getDecoder().decode(publicKeyString);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ECDSA);
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Could not extract public key due to: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public PrivateKey getPrivateKeyFromString(String privateKeyString) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyString.getBytes(StandardCharsets.UTF_8));
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ECDSA);
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.info("Could not extract private key due to: {}", e.getMessage(), e);
            return null;
        }
    }
}
