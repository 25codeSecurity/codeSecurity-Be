/*
파일명 : CertificateUtil.java
파일설명 : 공개키 추출 도움 유틸 코드(**인증서 대신 공개키 직접 파싱)
작성자 : 김소망
기간 : 2025-05-31
*/
package code_security.coin_futures.util;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/*public class CertificateUtil {
    public static PublicKey extractPublicKey(String certPem) throws Exception {
        String cleaned = certPem
                .replace("-----BEGIN CERTIFICATE-----", "")
                .replace("-----END CERTIFICATE-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(cleaned);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
}*/
public class CertificateUtil {
    public static PublicKey extractPublicKey(String certPem) throws Exception {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(certPem.getBytes(StandardCharsets.UTF_8));
        X509Certificate certificate = (X509Certificate) factory.generateCertificate(inputStream);

        certificate.checkValidity();
        return certificate.getPublicKey();
    }
}

