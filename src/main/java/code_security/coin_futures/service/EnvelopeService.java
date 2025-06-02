/*
파일명 : EnvelopeService.java
파일설명 : 전자봉투를 처리해서 계약 내용을 꺼내고 서명 검증 담당하는 서비스 코드
작성자 : 김소망
기간 : 2025-05-31
*/

package code_security.coin_futures.service;
import code_security.coin_futures.util.CertificateUtil;
import code_security.coin_futures.util.EnvelopeUtil;
import code_security.coin_futures.web.dto.FuturesContractDTO.EnvelopePayloadDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/*@Service
public class EnvelopeService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public DecodedContract unwrapEnvelope(byte[] encryptedPayload, byte[] encryptedKey, byte[] iv, PrivateKey serverPrivateKey) throws Exception {
        // AES 키 복호화
        SecretKey aesKey = EnvelopeUtil.decryptAESKey(encryptedKey, serverPrivateKey);

        // Payload 복호화
        byte[] decryptedBytes = EnvelopeUtil.decryptAES(encryptedPayload, aesKey, iv);
        String decryptedJson = new String(decryptedBytes, StandardCharsets.UTF_8);

        // JSON 파싱
        JsonNode root = objectMapper.readTree(decryptedJson);
        JsonNode contractNode = root.get("contract");
        String contractStr = objectMapper.writeValueAsString(contractNode);

        String signatureBase64 = root.get("signature").asText();
        byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);

        String certPEM = root.get("cert").asText();
        PublicKey userPublicKey = CertificateUtil.extractPublicKey(certPEM);

        // 서명 검증
        boolean valid = EnvelopeUtil.verifySignature(contractStr.getBytes(StandardCharsets.UTF_8), signatureBytes, userPublicKey);

        if (!valid) throw new SecurityException("전자서명 검증 실패");

        // 성공 시 계약 JSON과 공개키 반환
        return new DecodedContract(contractNode, userPublicKey);
    }

    public record DecodedContract(JsonNode contract, PublicKey userPublicKey) {}
}*/
@Service
public class EnvelopeService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public DecodedContract unwrapEnvelope(byte[] encryptedPayload, byte[] encryptedKey, byte[] iv, PrivateKey serverPrivateKey) throws Exception {
        // AES 키 복호화
        SecretKey aesKey = EnvelopeUtil.decryptAESKey(encryptedKey, serverPrivateKey);

        // Payload 복호화
        byte[] decryptedBytes = EnvelopeUtil.decryptAES(encryptedPayload, aesKey, iv);
        String decryptedJson = new String(decryptedBytes, StandardCharsets.UTF_8);

        // DTO로 역직렬화 (기존 JsonNode 제거)
        EnvelopePayloadDTO dto = objectMapper.readValue(decryptedJson, EnvelopePayloadDTO.class);

        // 서명 및 인증서 처리
        String contractStr = objectMapper.writeValueAsString(dto.getContract());
        byte[] signatureBytes = Base64.getDecoder().decode(dto.getSignature());
        PublicKey userPublicKey = CertificateUtil.extractPublicKey(dto.getCert());

        // 서명 검증
        boolean valid = EnvelopeUtil.verifySignature(contractStr.getBytes(StandardCharsets.UTF_8), signatureBytes, userPublicKey);
        if (!valid) throw new SecurityException("전자서명 검증 실패");

        return new DecodedContract(objectMapper.valueToTree(dto.getContract()), userPublicKey); // contract를 다시 JsonNode로 변환
    }

    public record DecodedContract(JsonNode contract, PublicKey userPublicKey) {}
}

