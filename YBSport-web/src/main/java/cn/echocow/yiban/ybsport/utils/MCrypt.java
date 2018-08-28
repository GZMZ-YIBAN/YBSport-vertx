/*
 *
 */
package cn.echocow.yiban.ybsport.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 解密方法
 * -----------------------------
 *
 * @author EchoLZY
 * @version 1.0
 * @date 13:52 2018/5/15
 * -----------------------------
 */
@SuppressWarnings("all")
public class MCrypt {

    private static char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private IvParameterSpec ivspec;
    private SecretKeySpec keyspec;
    private Cipher cipher;

    /**
     * 初始化易班应用密钥
     *
     * @param appId     应用 id
     * @param secretKey 应用密钥
     */
    public MCrypt(String appId, String secretKey) {
        ivspec = new IvParameterSpec(appId.getBytes());

        keyspec = new SecretKeySpec(secretKey.getBytes(), "AES");

        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加密
     *
     * @param text 加密字符
     * @return 加密后
     * @throws Exception 异常
     */
    public byte[] encrypt(String text) throws Exception {
        if (text == null || text.length() == 0) {
            throw new Exception("Empty string");
        }

        byte[] encrypted;

        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);

            encrypted = cipher.doFinal(padString(text).getBytes());
        } catch (Exception e) {
            throw new Exception("[encrypt] " + e.getMessage());
        }

        return encrypted;
    }

    /**
     * 解密
     *
     * @param code 解密字符
     * @return 解密后
     * @throws Exception 异常
     */
    public byte[] decrypt(String code) throws Exception {
        if (code == null || code.length() == 0) {
            throw new Exception("Empty string");
        }

        byte[] decrypted;

        try {
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            decrypted = cipher.doFinal(hexToBytes(code));
            if (decrypted.length > 0) {
                int trim = 0;
                for (int i = decrypted.length - 1; i >= 0; i--) {
                    if (decrypted[i] == 0) {
                        trim++;
                    }
                }

                if (trim > 0) {
                    byte[] newArray = new byte[decrypted.length - trim];
                    System.arraycopy(decrypted, 0, newArray, 0, decrypted.length - trim);
                    decrypted = newArray;
                }
            }
        } catch (Exception e) {
            throw new Exception("[decrypt] " + e.getMessage());
        }
        return decrypted;
    }


    public static String bytesToHex(byte[] buf) {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i) {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
    }


    private static byte[] hexToBytes(String str) {
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }
    }


    private static String padString(String source) {
        char paddingChar = 0;
        int size = 16;
        int x = source.length() % size;
        int padLength = size - x;

        StringBuilder sourceBuilder = new StringBuilder(source);
        for (int i = 0; i < padLength; i++) {
            sourceBuilder.append(paddingChar);
        }
        source = sourceBuilder.toString();

        return source;
    }
}