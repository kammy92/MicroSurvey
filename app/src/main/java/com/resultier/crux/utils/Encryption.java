package com.resultier.crux.utils;


import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {
    
    private static String LOG_TAG = "Encryption";
    private static boolean SHOW_LOG = true;
    private static String CHARSET = "UTF-8";
    
    private Cipher cipher;
    private byte[] key, iv;
    
    public Encryption () throws NoSuchAlgorithmException, NoSuchPaddingException {
        cipher = Cipher.getInstance ("AES/CBC/PKCS5Padding");
        key = new byte[16]; //128 bit key space
        iv = new byte[16]; //128 bit IV
    }
    
    public static String getSHA256 (String text, int length) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // length : 16 bytes => 128 bit, 32 bytes => 256 bit
        String output = "";
        Utils.showLog (Log.INFO, LOG_TAG, "Method : getSHA256()", SHOW_LOG);
        Utils.showLog (Log.INFO, LOG_TAG, "Input : " + text, SHOW_LOG);
        Utils.showLog (Log.INFO, LOG_TAG, "Length : " + length, SHOW_LOG);
        MessageDigest md = MessageDigest.getInstance ("SHA-256");
        md.update (text.getBytes (CHARSET));
        byte[] digest = md.digest ();
        StringBuilder result = new StringBuilder ();
        for (byte b : digest) {
            result.append (String.format ("%02x", b));
        }
        if (length > result.toString ().length ()) {
            output = result.toString ().toLowerCase ();
        } else {
            output = result.toString ().substring (16, length + 16).toLowerCase ();
        }
        Utils.showLog (Log.INFO, LOG_TAG, "Output : " + output, SHOW_LOG);
        return output;
    }
    
    public static String generateRandomIV (int length) {
        String iv = "";
        Utils.showLog (Log.INFO, LOG_TAG, "Method : generateRandomIV()", SHOW_LOG);
        SecureRandom ranGen = new SecureRandom ();
        byte[] aesKey = new byte[16];
        ranGen.nextBytes (aesKey);
        StringBuilder result = new StringBuilder ();
        for (byte b : aesKey) {
            result.append (String.format ("%02x", b));
        }
        if (length > result.toString ().length ()) {
            iv = result.toString ().toLowerCase ();
        } else {
            iv = result.toString ().substring (0, length).toLowerCase ();
        }
        Utils.showLog (Log.INFO, LOG_TAG, "Length : " + length, SHOW_LOG);
        Utils.showLog (Log.INFO, LOG_TAG, "Output : " + iv, SHOW_LOG);
        return iv;
    }
    
    private String encryption (String inputText, String encryptionKey, EncryptMode mode, String initVector) throws UnsupportedEncodingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        String output = "";
        Utils.showLog (Log.INFO, LOG_TAG, "Method : encryption()", SHOW_LOG);
        Utils.showLog (Log.INFO, LOG_TAG, "Input : " + inputText, SHOW_LOG);
        int keyLength = encryptionKey.getBytes (CHARSET).length;
        if (encryptionKey.getBytes (CHARSET).length > key.length) {
            keyLength = key.length;
        }
        Utils.showLog (Log.INFO, LOG_TAG, "Key Length : " + keyLength, SHOW_LOG);
    
        int ivLength = initVector.getBytes (CHARSET).length;
        if (initVector.getBytes (CHARSET).length > iv.length) {
            ivLength = iv.length;
        }
        Utils.showLog (Log.INFO, LOG_TAG, "IV Length : " + ivLength, SHOW_LOG);
    
    
        System.arraycopy (encryptionKey.getBytes (CHARSET), 0, key, 0, keyLength);
        System.arraycopy (initVector.getBytes (CHARSET), 0, iv, 0, ivLength);
    
        SecretKeySpec keySpec = new SecretKeySpec (key, "AES"); // Create a new SecretKeySpec
        IvParameterSpec ivSpec = new IvParameterSpec (iv);
        
        // Encryption
        if (mode.equals (EncryptMode.ENCRYPT)) {
            cipher.init (Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] results = cipher.doFinal (inputText.getBytes (CHARSET));
            output = Base64.encodeToString (results, Base64.DEFAULT);
        }
        
        // Decryption
        if (mode.equals (EncryptMode.DECRYPT)) {
            cipher.init (Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decodedValue = Base64.decode (inputText.getBytes (), Base64.DEFAULT);
            byte[] decryptedVal = cipher.doFinal (decodedValue);
            output = new String (decryptedVal);
        }
        Utils.showLog (Log.INFO, LOG_TAG, "Output : " + output, SHOW_LOG);
        return output;
    }
    
    public String encrypt (String plainText, String key, String iv) throws InvalidKeyException, UnsupportedEncodingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Utils.showLog (Log.INFO, LOG_TAG, "Method : encrypt()", SHOW_LOG);
        Utils.showLog (Log.INFO, LOG_TAG, "Input : " + plainText, SHOW_LOG);
        Utils.showLog (Log.INFO, LOG_TAG, "Key : " + key, SHOW_LOG);
        Utils.showLog (Log.INFO, LOG_TAG, "IV : " + iv, SHOW_LOG);
        return encryption (plainText, key, EncryptMode.ENCRYPT, iv);
    }
    
    public String decrypt (String encryptedText, String key, String iv) throws InvalidKeyException, UnsupportedEncodingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Utils.showLog (Log.INFO, LOG_TAG, "Method : decrypt()", SHOW_LOG);
        Utils.showLog (Log.INFO, LOG_TAG, "Input : " + encryptedText, SHOW_LOG);
        Utils.showLog (Log.INFO, LOG_TAG, "Key : " + key, SHOW_LOG);
        Utils.showLog (Log.INFO, LOG_TAG, "IV : " + iv, SHOW_LOG);
        return encryption (encryptedText, key, EncryptMode.DECRYPT, iv);
    }
    
    private enum EncryptMode {
        ENCRYPT, DECRYPT
    }
}