package com.example.moupass10;

import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;

public class CryptoUtils {

    private static final String AES = "AES";
    private static final String AES_CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";

    public static Key generateKey() throws GeneralSecurityException {
        SecureRandom secureRandom = new SecureRandom();
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES, new BouncyCastleProvider());
        keyGenerator.init(256, secureRandom);
        return keyGenerator.generateKey();
    }

    public static byte[] doAES(int encryptMode, Key secretKey, byte[] iv, byte[] bytes)
            throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM, new BouncyCastleProvider());
        cipher.init(encryptMode, secretKey, new IvParameterSpec(iv));
        return cipher.doFinal(bytes);
    }
}

