package com.github.makewheels;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

@Service
@Slf4j
public class KeyService {
    @Value("${spring.application.name}")
    private String applicationName;

    private PublicPrivateKey convertKeyPairToPublicPrivateKey(KeyPair keyPair) {
        if (keyPair == null)
            return null;
        String publicKey = RSAUtil.keyToBase64(keyPair.getPublic());
        String privateKey = RSAUtil.keyToBase64(keyPair.getPrivate());
        PublicPrivateKey publicPrivateKey = new PublicPrivateKey();
        publicPrivateKey.setPublicKey(publicKey);
        publicPrivateKey.setPrivateKey(privateKey);
        return publicPrivateKey;
    }

    public PublicPrivateKey generateKeyPair() {
        KeyPair keyPair = RSAUtil.generateKeyPair();
        return convertKeyPairToPublicPrivateKey(keyPair);
    }

    public PublicPrivateKey generateKeyPairAndSave() {
        KeyPair keyPair = RSAUtil.generateKeyPair();
        if (keyPair == null)
            return null;
        SecretKeyUtil.savePublicKey(keyPair.getPublic());
        SecretKeyUtil.savePrivateKey(keyPair.getPrivate());
        return convertKeyPairToPublicPrivateKey(keyPair);
    }

    public String encrypt(String data) {
        PublicKey publicKey;
        try {
            publicKey = SecretKeyUtil.loadPublicKey();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        try {
            return RSAUtil.encrypt(data, publicKey);
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException
                | NoSuchPaddingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public String decrypt(String data) {
        PrivateKey privateKey;
        try {
            privateKey = SecretKeyUtil.loadPrivateKey();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        try {
            return RSAUtil.decrypt(data, privateKey);
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException
                | NoSuchPaddingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
