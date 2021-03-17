package com.github.makewheels;

import lombok.extern.slf4j.Slf4j;
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
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;

/**
 * @Author makewheels
 * @Time 2021.03.16 22:01:00
 */
@Slf4j
public class SecretKeyUtil {

    /**
     * 替换单个配置文件
     *
     * @param propertiesFile
     * @param privateKey
     */
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
            //找到加密字段，解密替换
            String prefix = "CIPHER ";
            String value = config.get(String.class, key);
            if (value.startsWith(prefix)) {
                value = value.replaceFirst(prefix, "");
                String decrypt;
                try {
                    decrypt = RSAUtil.decrypt(value, privateKey);
                } catch (NoSuchPaddingException | NoSuchAlgorithmException
                        | InvalidKeyException | BadPaddingException
                        | IllegalBlockSizeException e) {
                    e.printStackTrace();
                    return;
                }
                config.setProperty(key, decrypt);
            }
        });
        //最后保存文件
        try {
            layout.save(config, new FileWriter(propertiesFile, false));
        } catch (ConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取配置文件中的应用名
     *
     * @return
     */
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

    /**
     * 获取存秘钥文件的根目录
     *
     * @return
     */
    public static File getKeyFolder() {
        return new File(SystemUtils.getUserHome(), "keys");
    }

    /**
     * 加载本地私钥
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws InvalidKeySpecException
     */
    public static PrivateKey loadPrivateKey()
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        File keyFile = new File(getKeyFolder(),  getApplicationName() + ".privateKey");
        if (!keyFile.exists()) {
            log.info("secret key not exist: {}", keyFile.getPath());
            return null;
        }
        return RSAUtil.loadPrivateKey(keyFile);
    }

    /**
     * 加载本地公钥
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PublicKey loadPublicKey()
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        File keyFile = new File(getKeyFolder(), getApplicationName() + ".publicKey");
        if (!keyFile.exists()) {
            log.info("public key not exist: {}", keyFile.getPath());
            return null;
        }
        return RSAUtil.loadPublicKey(keyFile);
    }

    /**
     * 暴露方法，复写本地keys
     */
    public static void overrideKeys() {
        //列出所有文件
        File[] files = new File(SecretKeyUtil.class.getResource("/").getPath()).listFiles();
        if (files == null)
            return;

        //加载本地私钥文件
        PrivateKey privateKey;
        try {
            privateKey = loadPrivateKey();
        } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
            e.printStackTrace();
            return;
        }
        if (privateKey == null)
            return;

        //遍历所有配置文件，逐一替换
        Arrays.stream(files).filter(file -> {
            if (!file.exists())
                return false;
            String fileName = file.getName();
            if (StringUtils.isEmpty(fileName))
                return false;
            return fileName.startsWith("application") && fileName.endsWith(".properties");
        }).forEach(file -> handleSingleFile(file, privateKey));
    }
}
