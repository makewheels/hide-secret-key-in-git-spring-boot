package com.github.makewheels;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyPair;

@Service
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


}
