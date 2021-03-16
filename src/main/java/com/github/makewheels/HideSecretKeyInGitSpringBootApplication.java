package com.github.makewheels;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfigurationLayout;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.crypto.Cipher;
import java.io.*;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class HideSecretKeyInGitSpringBootApplication {

    public static void main(String[] args) {
        handleKeys(args);
        SpringApplication.run(HideSecretKeyInGitSpringBootApplication.class, args);
    }

    //-decryptKey=abc
    private static void handleKeys(String[] args) {
        List<String> argList = Arrays.stream(args)
                .filter(each -> StringUtils.isNotEmpty(each) && each.startsWith("-decryptKey"))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(argList))
            return;
        String decryptKey = argList.get(0).split("=")[1];

        String path = HideSecretKeyInGitSpringBootApplication.class
                .getResource("/application.properties").getPath();
        File propertiesFile = new File(path);
        PropertiesConfiguration config = new PropertiesConfiguration();
        PropertiesConfigurationLayout layout = config.getLayout();
        try {
            layout.load(config, new InputStreamReader(new FileInputStream(propertiesFile)));
            List<String> keys = IteratorUtils.toList(config.getKeys());
            keys.stream().filter(key -> {
                if (StringUtils.isEmpty(key))
                    return false;
                String value = config.get(String.class, key);
                return !StringUtils.isEmpty(value);
            }).forEach(key -> {
                String value = config.get(String.class, key);
                if (value.startsWith("$CIPHER$")) {
                    config.setProperty(key, System.currentTimeMillis());
                }
            });
            layout.save(config, new FileWriter(propertiesFile, false));
        } catch (ConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 私钥解密
     */
    public static byte[] decryptByPrivateKey(byte[] data, byte[] key)
            throws Exception {
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        // 生成私钥
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        int blockSize = cipher.getBlockSize();
        if (blockSize > 0) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream(64);
            int j = 0;
            while (data.length - j * blockSize > 0) {
                bout.write(cipher.doFinal(data, j * blockSize, blockSize));
                j++;
            }
            return bout.toByteArray();
        }
        return cipher.doFinal(data);
    }
}