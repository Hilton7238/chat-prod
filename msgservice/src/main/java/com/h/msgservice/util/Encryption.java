package com.h.msgservice.util;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class Encryption {
    private static final String key = "@&*qwe!#";

    public String desEncrypt(String content) throws Exception {
        //指定加密算法、加密模式、填充模式
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        //创建加密规则：指定key和加密类型
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "DES");
        //指定加密模式为加密，指定加密规则
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        //调用加密方法
        byte[] result = cipher.doFinal(content.getBytes());
        //用Base64编码
        return new String(Base64.getEncoder().encode(result));
    }

    /**
     * DES解密
     *
     * @param content 待解密数据
     * @return
     * @throws Exception
     */
    public String desDecrypt(String content) throws Exception {
        //Base64解码
        byte[] result = Base64.getDecoder().decode(content);
        //指定加密算法、加密模式、填充模式
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        //创建加密规则：指定key和加密类型
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "DES");
        //指定加密模式为解密，指定加密规则
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return new String(cipher.doFinal(result));
    }
}
