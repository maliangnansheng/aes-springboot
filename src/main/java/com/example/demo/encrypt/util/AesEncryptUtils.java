package com.example.demo.encrypt.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * AES 加/解密工具类
 *
 * @author 马亮
 * @date 2021/3/9 16:13
 */
@Slf4j
public class AesEncryptUtils {

    /**
     * 密匙
     */
    private static final String KEY = "d86d7bab3d6ac01ad9dc6a897652f2d2";
    /**
     * 偏移量
     */
    private static final String IV = "0123456789abcdef";
    /**
     * 算法
     */
    private static final String ALGORITHM = "AES";
    /**
     * 加密算法
     */
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    /**
     * 密钥的默认位长度
     */
    public static final int DEFAULT_KEY_SIZE = 128;


    /**
     * 加密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static String aesEncrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

        // 使用Hex做转码
        return Hex.encodeHexString(encrypted);
    }

    /**
     * 解密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static String aesDecrypt(String data) throws Exception {
        // 密文使用Hex解码
        byte[] content = Hex.decodeHex(data);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
        byte[] decrypted = cipher.doFinal(content);

        return new String(decrypted, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws Exception {

        String content = "你好";
        log.info("加密前 {}", content);

        String encrypt = aesEncrypt(content);
        log.info("{} 加密后 {}", encrypt.length(), encrypt);

        String decrypt = aesDecrypt(encrypt);
        log.info("解密后 {}", decrypt);
    }

}