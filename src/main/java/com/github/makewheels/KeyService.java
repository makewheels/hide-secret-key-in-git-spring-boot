package com.github.makewheels;

import org.springframework.stereotype.Service;

import java.security.KeyPair;

@Service
public class KeyService {
    public PublicPrivateKey generateKeyPair() {
        KeyPair keyPair = RSAUtil.generateKeyPair();
        if (keyPair == null)
            return null;
        String publicKey = RSAUtil.publicKeyToBase64(keyPair.getPublic());
        String privateKey = RSAUtil.privateKeyToBase64(keyPair.getPrivate());
        PublicPrivateKey publicPrivateKey = new PublicPrivateKey();
        publicPrivateKey.setPublicKey(publicKey);
        publicPrivateKey.setPrivateKey(privateKey);
        return publicPrivateKey;
    }
}
