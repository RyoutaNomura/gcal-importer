package com.rn.tool.gcalimporter.utils;

import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class EncryptlUtils {

  private static final String KEY = "key";

  public static byte[] encrypt(final Path dest, final byte[] data) {

    try {
      final Key sksSpec = new SecretKeySpec(KEY.getBytes(), "Blowfish");
      final Cipher cipher = Cipher.getInstance("Blowfish");

      cipher.init(Cipher.ENCRYPT_MODE, sksSpec);
      return cipher.doFinal(data);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
        | BadPaddingException e) {
      throw new RuntimeException(e);
    }
  }

  public static byte[] decrypt(final byte[] data) {

    try {
      final Key sksSpec = new SecretKeySpec(KEY.getBytes(), "Blowfish");
      final Cipher cipher = Cipher.getInstance("Blowfish");

      cipher.init(Cipher.DECRYPT_MODE, sksSpec);
      return cipher.doFinal(data);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
        | BadPaddingException e) {
      throw new RuntimeException(e);

    }
  }
}