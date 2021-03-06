package com.rn.tool.gcalimporter.utils;

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;
import org.junit.Test;

public class CredentialUtilsTest {

  final private byte[] str = "abcdefg1234567".getBytes();

  @Test
  public void testEncrypt() {

    final byte[] encstr = EncryptUtils.encrypt(Paths.get(""), this.str);
    System.out.println(new String(encstr));
  }

  @Test
  public void testDecrypt() {

    final byte[] encstr = EncryptUtils.encrypt(Paths.get(""), this.str);
    System.out.println(new String(encstr));

    final byte[] decstr = EncryptUtils.decrypt(encstr);
    System.out.println(new String(decstr));

    assertEquals(new String(this.str), new String(decstr));
  }

}
