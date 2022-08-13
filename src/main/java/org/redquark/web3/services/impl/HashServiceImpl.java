package org.redquark.web3.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.redquark.web3.services.HashService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

@Service
@Slf4j
public class HashServiceImpl implements HashService {

    @Override
    public String createSHA256Hash(String input) {
        try {
            // MessageDigest is not thread-safe, so we should be using a new instance
            // for every thread
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            // Applies SHA-256 to our input
            byte[] hash = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));
            // This will contain hash as a hexadecimal string
            log.info("Converting the bytes to hexadecimal string");
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("Could not apply SHA-256 hash to the input string due to: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    @Override
    public byte[] applyECDSASignature(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output;
        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] inputBytes = input.getBytes();
            dsa.update(inputBytes);
            output = dsa.sign();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
            log.error("Could not apply Elliptic Curve DSA on the input due to: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return output;
    }

    @Override
    public boolean verifyECDSASignature(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
            log.error("Could not verify Elliptic Curve DSA on the input due to: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
