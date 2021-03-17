package com.example.demo.encrypt.constant;

/**
 * @author 马亮
 * @date 2021/3/12 16:34
 */
public class HeaderConstants {
    /**
     * 敏感信息解密头
     */
    public static final String DECRYPT_HEADER = "X-REQ-ENCRYPTED";

    /**
     * 敏感信息加密头
     */
    public static final String ENCRYPT_HEADER = "X-RES-ENCRYPTED";

    /**
     * 加密的参数
     */
    public static final String HAVE_ENCRYPT_PARAMS = "X-HAVE-ENCRYPT";

    /**
     * 未加密的参数
     */
    public static final String NOT_ENCRYPT_PARAMS = "X-NOT-ENCRYPT";

}
