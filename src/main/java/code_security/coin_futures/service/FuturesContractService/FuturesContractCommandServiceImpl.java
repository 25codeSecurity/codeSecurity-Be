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
}
