package com.github.makewheels;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfigurationLayout;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;

/**
 * @Author makewheels
 * @Time 2021.03.16 22:01:00
 */
public class SecretKeyUtil {
    private static void handleSingleFile(File propertiesFile, PrivateKey privateKey) {
        PropertiesConfiguration config = new PropertiesConfiguration();
        PropertiesConfigurationLayout layout = config.getLayout();
        try {
            layout.load(config, new FileReader(propertiesFile));
        } catch (ConfigurationException | FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        List<String> keys = IteratorUtils.toList(config.getKeys());
        keys.stream().filter(key -> {
            if (StringUtils.isEmpty(key))
                return false;
            String value = config.get(String.class, key);
            return StringUtils.isNotEmpty(value);
        }).forEach(key -> {
            String prefix = "CIPHER";
            String value = config.get(String.class, key);
            if (value.startsWith(prefix)) {
                value = value.replaceFirst(prefix, "");
                byte[] decryptBytes;
                try {
                    decryptBytes = RSAUtil.decrypt(value.getBytes(), privateKey);
                } catch (NoSuchPaddingException | NoSuchAlgorithmException
                        | InvalidKeyException | BadPaddingException
                        | IllegalBlockSizeException e) {
                    e.printStackTrace();
                    return;
                }
                config.setProperty(key, new String(decryptBytes));
            }
        });
        try {
            layout.save(config, new FileWriter(propertiesFile, false));
        } catch (ConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    private static String getApplicationName() {
        PropertiesConfiguration config = new PropertiesConfiguration();
        PropertiesConfigurationLayout layout = config.getLayout();
        File propertiesFile = new File(SecretKeyUtil.class
                .getResource("/application.properties").getPath());
        try {
            layout.load(config, new FileReader(propertiesFile));
        } catch (ConfigurationException | FileNotFoundException e) {
            e.printStackTrace();
        }
        return config.get(String.class, "spring.application.name");
    }

    private static PrivateKey loadPrivateKey()
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        File folder = new File(SystemUtils.getUserHome(), "keys");
        String filename = getApplicationName() + ".privateKey";
        File privateKeyFile = new File(folder, filename);
        if (!privateKeyFile.exists())
            return null;
        return RSAUtil.loadPrivateKey(privateKeyFile);
    }

    public static void overrideKeys() {
        PrivateKey privateKey;
        try {
            privateKey = loadPrivateKey();
        } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
            e.printStackTrace();
            return;
        }
        if (privateKey == null)
            return;

        File[] files = new File(SecretKeyUtil.class.getResource("/").getPath()).listFiles();
        if (files == null)
            return;

        Arrays.stream(files).filter(file -> {
            String fileName = file.getName();
            if (StringUtils.isEmpty(fileName))
                return false;
            return fileName.startsWith("application") && fileName.endsWith(".properties");
        }).forEach(file -> SecretKeyUtil.handleSingleFile(file, privateKey));
    }
}
