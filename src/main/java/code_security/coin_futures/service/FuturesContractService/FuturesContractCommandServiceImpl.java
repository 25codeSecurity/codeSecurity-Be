/*
파일명 : FuturesContractService.java
파일설명 : 계약 조건 비교해서 계약 체결하고 DB 저장하는 서비스 코드
작성자 : 김소망
기간 : 2025-05-31
*/
package code_security.coin_futures.service.FuturesContractService;

import code_security.coin_futures.domain.FuturesContract;
import code_security.coin_futures.domain.Member;
import code_security.coin_futures.repository.FuturesContractRepository.FuturesContractRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FuturesContractCommandServiceImpl implements FuturesContractCommandService {

    private final FuturesContractRepository futuresContractRepository;
    private final EnvelopeService envelopeService;

    public void matchAndSaveContracts(
            byte[] payloadA, byte[] keyA, byte[] ivA, Member memberA,
            byte[] payloadB, byte[] keyB, byte[] ivB, Member memberB,
            PrivateKey serverPrivateKey
    ) throws Exception {

        EnvelopeService.DecodedContract decodedA = envelopeService.unwrapEnvelope(payloadA, keyA, ivA, serverPrivateKey);
        EnvelopeService.DecodedContract decodedB = envelopeService.unwrapEnvelope(payloadB, keyB, ivB, serverPrivateKey);

        JsonNode a = decodedA.contract();
        JsonNode b = decodedB.contract();

        boolean matched =
                a.get("asset").asText().equals(b.get("asset").asText()) &&
                        a.get("amount").asDouble() == b.get("amount").asDouble() &&
                        a.get("strike_price").asDouble() == b.get("strike_price").asDouble() &&
                        a.get("expiration").asText().equals(b.get("expiration").asText()) &&
                        !a.get("position").asText().equals(b.get("position").asText());

        if (!matched) {
            throw new IllegalArgumentException("계약 조건이 일치하지 않습니다.");
        }

        FuturesContract contractA = FuturesContract.builder()
                .type(a.get("type").asText())
                .position(a.get("position").asText())
                .asset(a.get("asset").asText())
                .amount(a.get("amount").asDouble())
                .strikePrice(a.get("strike_price").asDouble())
                .expiration(LocalDate.parse(a.get("expiration").asText()))
                .user(a.get("user").asText())
                .timestamp(LocalDateTime.parse(a.get("timestamp").asText()))
                .digitalEnvelope(payloadA)
                .user(memberA.toString())
                .build();

        FuturesContract contractB = FuturesContract.builder()
                .type(b.get("type").asText())
                .position(b.get("position").asText())
                .asset(b.get("asset").asText())
                .amount(b.get("amount").asDouble())
                .strikePrice(b.get("strike_price").asDouble())
                .expiration(LocalDate.parse(b.get("expiration").asText()))
                .user(b.get("user").asText())
                .timestamp(LocalDateTime.parse(b.get("timestamp").asText()))
                .digitalEnvelope(payloadB)
                .user(memberB.toString())
                .build();

        futuresContractRepository.save(contractA);
        futuresContractRepository.save(contractB);

        System.out.println("✅ 계약 쌍 체결 및 저장 완료");
    }
}

