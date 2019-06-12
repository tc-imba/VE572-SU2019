package com.ve572.p1.preprocess;

import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CheckSum {

    public static String getSHA1(byte[] bytes) {
        String checksum = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(bytes);
            checksum = Hex.encodeHexString(digest.digest()).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return checksum;
    }

}
