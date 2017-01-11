package net.bingyan.hustpass.scanner.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by lwenkun on 2016/12/17.
 */

public class MD5 {

    /**
     *
     * @param raw
     * @return
     */
    public static String string2MD5 (String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(raw.getBytes());
            byte[] bytes = digest.digest();
            return bytes2MD5(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param bytes
     * @return
     */
    public static String bytes2MD5(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0x000000ff);
            if (hex.length() == 1) {
                sb.append("0");
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
