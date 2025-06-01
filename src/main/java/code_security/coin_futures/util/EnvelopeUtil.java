/*
파일명 : EnvelopeUtil.java
파일설명 : 전자봉투 처리 유틸 코드
작성자 : 김소망
기간 : 2025-05-31
*/
package code_security.coin_futures.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class EnvelopeUtil {

    // AES 복호화
    public static byte[] decryptAES(byte[] encryptedData, SecretKey secretKey, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        return cipher.doFinal(encryptedData);
    }

    // RSA 개인키로 AES 키 복호화
    public static SecretKey decryptAESKey(byte[] encryptedKey, PrivateKey privateKey) throws Exception {
        Cipher rsa = Cipher.getInstance("RSA");
        rsa.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] secretKeyBytes = rsa.doFinal(encryptedKey);
        return new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length, "AES");
    }

    // 전자서명 검증
    public static boolean verifySignature(byte[] data, byte[] signatureBytes, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(signatureBytes);
    }
}

