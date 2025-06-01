package code_security.coin_futures.service.FuturesContractService;

import code_security.coin_futures.crypto.CryptoUtil;
import code_security.coin_futures.domain.FuturesContract;
import code_security.coin_futures.repository.FuturesContractRepository.FuturesContractRepository;
import code_security.coin_futures.service.KeyStoreService.KeyStoreService;
import code_security.coin_futures.web.dto.FuturesContractDTO.FuturesContractRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FuturesContractCommandServiceImpl implements FuturesContractCommandService {

    private FuturesContractRepository futuresContractRepository;
    private KeyStoreService keyStoreService;

    @Override
    public void submitContract(FuturesContractRequestDTO.SubmitContractDTO request, Long userId) throws Exception{
        // 1. 계약 JSON 생성
        String contractJson = new ObjectMapper().writeValueAsString(request);

        // 2. 사용자 개인키로 서명
        PrivateKey userPrivateKey = keyStoreService.getUserPrivateKey(userId);
        byte[] signature = CryptoUtil.sign(contractJson.getBytes(), userPrivateKey);

        // 3. 인증서 첨부 (예: PEM 파일로부터 읽어오기)
        //byte[] certification = keyStoreService.getUserCertificate(userId);

        // 4. AES 대칭키 생성
        SecretKey aesKey = CryptoUtil.generateAESKey();

        // 5. Payload 암호화
        ByteArrayOutputStream payloadStream = new ByteArrayOutputStream();
        payloadStream.write(contractJson.getBytes());
        payloadStream.write(signature);
        byte[] encryptedPayload = CryptoUtil.encryptAES(payloadStream.toByteArray(), aesKey);

        // 6. AES키를 서버의 공개키로 RSA 암호화
        PublicKey serverPublicKey = keyStoreService.getServerPublicKey();
        byte[] encryptedKey = CryptoUtil.encryptRSA(aesKey.getEncoded(), serverPublicKey);

        // 7. 전자봉투 생성
        ByteArrayOutputStream envelopeStream = new ByteArrayOutputStream();
        envelopeStream.write(encryptedPayload);
        envelopeStream.write(encryptedKey);
        byte[] digitalEnvelope = envelopeStream.toByteArray();

        // 8. 저장
        FuturesContract contract = FuturesContract.builder()
                .type(request.getType())
                .position(request.getPosition())
                .asset(request.getAsset())
                .amount(request.getAmount())
                .strikePrice(request.getStrikePrice())
                .expiration(request.getExpiration())
                .build();

        futuresContractRepository.save(contract);
    }

    // 계약 매칭 (A, B 두 계약서 조건 비교 → 체결) 김소망이 추가
    @Override
    public void matchContracts(Long contractId1, Long contractId2) {
        FuturesContract c1 = futuresContractRepository.findById(contractId1)
                .orElseThrow(() -> new RuntimeException("계약 1번 없음"));
        FuturesContract c2 = futuresContractRepository.findById(contractId2)
                .orElseThrow(() -> new RuntimeException("계약 2번 없음"));

        boolean matched =
                c1.getAsset().equals(c2.getAsset()) &&
                        c1.getAmount().equals(c2.getAmount()) &&
                        c1.getStrikePrice().equals(c2.getStrikePrice()) &&
                        c1.getExpiration().equals(c2.getExpiration()) &&
                        !c1.getPosition().equals(c2.getPosition()); // Long vs Short

        if (!matched) {
            throw new IllegalArgumentException("계약 조건 불일치");
        }

        c1.setMatched(true);
        c2.setMatched(true);
        futuresContractRepository.saveAll(List.of(c1, c2));

        System.out.println("✅ 계약 체결 성공 및 상태 저장 완료: ID " + c1.getId() + " ↔ " + c2.getId());
    }



}