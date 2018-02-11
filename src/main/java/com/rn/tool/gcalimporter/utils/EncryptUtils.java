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
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Class for utility of Encryption
 */
@UtilityClass
public class EncryptUtils {

  private static final String KEY = "key";

  public static byte[] encrypt(@NonNull final Path dest, @NonNull final byte[] data) {

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

  public static byte[] decrypt(@NonNull final byte[] data) {

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