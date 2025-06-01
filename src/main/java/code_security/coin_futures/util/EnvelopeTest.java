/*
파일명 : EnvelopeTest.java
파일설명 : 전자봉투 생성,복호화, 서명 검증 테스트 코드
작성자 : 김소망
기간 : 2025-05-31
*/
package code_security.coin_futures.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class EnvelopeTest {

    public static void main(String[] args) throws Exception {
        // 계약 데이터
        String contractJson = new ObjectMapper().writeValueAsString(Map.of(
                "type", "futures_contract",
                "position", "long",
                "asset", "BTC",
                "amount", 1.0,
                "strike_price", 60000,
                "expiration", "2025-06-30",
                "user", "userA",
                "timestamp", "2025-05-20T10:00:00Z"
        ));

        // 사용자 키쌍 (서명용)
        KeyPair userKeyPair = generateRSAKeyPair();
        PrivateKey userPrivateKey = userKeyPair.getPrivate();
        PublicKey userPublicKey = userKeyPair.getPublic();

        // 서명 생성
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(userPrivateKey);
        signature.update(contractJson.getBytes(StandardCharsets.UTF_8));
        byte[] signatureBytes = signature.sign();
        String signatureBase64 = Base64.getEncoder().encodeToString(signatureBytes);

        // 인증서 (시뮬레이션) — 공개키를 그냥 String으로 보냄
        String certPEM = pemEncodePublicKey(userPublicKey);

        // JSON 묶기
        Map<String, Object> envelopeContent = new HashMap<>();
        envelopeContent.put("contract", new ObjectMapper().readTree(contractJson));
        envelopeContent.put("signature", signatureBase64);
        envelopeContent.put("cert", certPEM);

        String payloadJson = new ObjectMapper().writeValueAsString(envelopeContent);
        byte[] payloadBytes = payloadJson.getBytes(StandardCharsets.UTF_8);

        // AES 키 생성 (대칭키)
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey aesKey = keyGen.generateKey();
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // payload 암호화 (AES)
        Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec);
        byte[] encryptedPayload = aesCipher.doFinal(payloadBytes);

        // AES 키를 서버 공개키로 암호화
        KeyPair serverKeyPair = generateRSAKeyPair();
        PublicKey serverPublicKey = serverKeyPair.getPublic();
        PrivateKey serverPrivateKey = serverKeyPair.getPrivate();

        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
        byte[] encryptedAESKey = rsaCipher.doFinal(aesKey.getEncoded());

        // ============== 서버 복호화 테스트 ============== //

        // AES 키 복호화
        SecretKey decryptedAESKey = EnvelopeUtil.decryptAESKey(encryptedAESKey, serverPrivateKey);
        byte[] decryptedPayload = EnvelopeUtil.decryptAES(encryptedPayload, decryptedAESKey, iv);

        // JSON 파싱
        String decryptedJsonStr = new String(decryptedPayload, StandardCharsets.UTF_8);
        System.out.println("🔓 복호화된 Payload:\n" + decryptedJsonStr);

        Map<String, Object> resultMap = new ObjectMapper().readValue(decryptedJsonStr, Map.class);
        String contractStr = new ObjectMapper().writeValueAsString(resultMap.get("contract"));
        byte[] signatureDecoded = Base64.getDecoder().decode((String) resultMap.get("signature"));
        String certPem = (String) resultMap.get("cert");

        PublicKey extractedKey = CertificateUtil.extractPublicKey(certPem);

        boolean verified = EnvelopeUtil.verifySignature(contractStr.getBytes(StandardCharsets.UTF_8), signatureDecoded, extractedKey);
        System.out.println("✅ 서명 검증 결과: " + verified);
    }

    // RSA 키쌍 생성
    public static KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    // 공개키를 PEM 문자열로 변환
    public static String pemEncodePublicKey(PublicKey publicKey) {
        String base64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        return "-----BEGIN CERTIFICATE-----\n" + base64 + "\n-----END CERTIFICATE-----";
    }
}

