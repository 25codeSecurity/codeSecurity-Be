package code_security.coin_futures.service.FuturesContractService;

import code_security.coin_futures.crypto.CryptoUtil;
import code_security.coin_futures.domain.FuturesContract;
import code_security.coin_futures.domain.Member;
import code_security.coin_futures.repository.FuturesContractRepository;
import code_security.coin_futures.repository.MemberRepository;
import code_security.coin_futures.service.KeyStoreService.KeyStoreService;
import code_security.coin_futures.web.dto.FuturesContractDTO.FuturesContractRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FuturesContractCommandServiceImpl implements FuturesContractCommandService {

    private final FuturesContractRepository futuresContractRepository;
    private final KeyStoreService keyStoreService;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void submitContract(FuturesContractRequestDTO.SubmitContractDTO request, Long userId) throws Exception{

        System.out.println("DTO 내용: " + request.toString());

        System.out.println("== 계약 제출 시작 ==");

        // 멤버 객체 가져오기
        Member member = memberRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("Member not found")
        );
        System.out.println("1. 사용자 로드 완료: " + member.getName());

        // 계약 JSON 생성
        ObjectMapper mapper = new ObjectMapper();
// 필요한 설정 추가
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.registerModule(new JavaTimeModule());  // LocalDateTime 등 처리

        String contractJson = null;
        try {
            contractJson = mapper.writeValueAsString(request);
            System.out.println("2. 계약 JSON 생성 완료:\n" + contractJson);
        } catch (Exception e) {
            System.err.println("JSON 변환 실패: " + e.getMessage());
            e.printStackTrace();
            throw e;  // 또는 적절한 예외 처리
        }

        // 사용자 개인키로 서명
        PrivateKey userPrivateKey = keyStoreService.getUserPrivateKey(userId);
        System.out.println("3. 사용자 개인키 로드 완료");

        byte[] signature = CryptoUtil.sign(contractJson.getBytes(), userPrivateKey);
        System.out.println("4. 서명 완료, 서명 길이: " + signature.length);

        // AES 대칭키 생성
        SecretKey aesKey = CryptoUtil.generateAESKey();
        System.out.println("5. AES 대칭키 생성 완료");

        // Payload 암호화
        ByteArrayOutputStream payloadStream = new ByteArrayOutputStream();
        payloadStream.write(contractJson.getBytes());
        payloadStream.write(signature);

        byte[] encryptedPayload = CryptoUtil.encryptAES(payloadStream.toByteArray(), aesKey);
        System.out.println("6. Payload AES 암호화 완료, 길이: " + encryptedPayload.length);

        // AES 키를 서버 공개키로 암호화
        PublicKey serverPublicKey = keyStoreService.getServerPublicKey();
        System.out.println("7. 서버 공개키 로드 완료");

        byte[] encryptedKey = CryptoUtil.encryptRSA(aesKey.getEncoded(), serverPublicKey);
        System.out.println("8. AES키 RSA 암호화 완료, 길이: " + encryptedKey.length);

        // 전자봉투 생성
        ByteArrayOutputStream envelopeStream = new ByteArrayOutputStream();
        envelopeStream.write(encryptedPayload);
        envelopeStream.write(encryptedKey);
        byte[] digitalEnvelope = envelopeStream.toByteArray();
        System.out.println("9. 전자봉투 생성 완료, 총 길이: " + digitalEnvelope.length);

        // DB 저장
        FuturesContract contract = FuturesContract.builder()
                .type(request.getType())
                .position(request.getPosition())
                .asset(request.getAsset())
                .amount(request.getAmount())
                .strikePrice(request.getStrikePrice())
                .expiration(request.getExpiration())
                .timestamp(LocalDateTime.now())
                .member(member)
                .matchCode(request.getMatchCode())
                .digitalEnvelope(digitalEnvelope)
                .matchedContract(null)
                .build();

        FuturesContract savedContract = futuresContractRepository.save(contract);
        futuresContractRepository.flush();

        System.out.println("🔒 저장 완료! 계약 ID: " + savedContract.getId());
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

        //System.out.println("✅ 계약 체결 성공 및 상태 저장 완료: ID " + c1.getId() + " ↔ " + c2.getId());
        log.info("계약 체결 성공 - contract1: {}, contract2: {}", c1.getId(), c2.getId());
    }
}

//
//
//    @Override
//    @Transactional
//    public void submitContract(FuturesContractRequestDTO.SubmitContractDTO request, Long userId) throws Exception{
//
//        //멤버 객체 가져오기
//        Member member = memberRepository.findById(userId).orElseThrow(
//                () -> new RuntimeException("Member not found")
//        );
//
//        // 1. 계약 JSON 생성
//        String contractJson = new ObjectMapper().writeValueAsString(request);
//
//        // 2. 사용자 개인키로 서명
//        PrivateKey userPrivateKey = keyStoreService.getUserPrivateKey(userId);
//        byte[] signature = CryptoUtil.sign(contractJson.getBytes(), userPrivateKey);
//
//        // 3. (선택) 인증서 첨부
//        // byte[] certification = keyStoreService.getUserCertificate(userId);
//
//        // 4. AES 대칭키 생성
//        SecretKey aesKey = CryptoUtil.generateAESKey();
//
//        // 5. Payload 암호화
//        ByteArrayOutputStream payloadStream = new ByteArrayOutputStream();
//        payloadStream.write(contractJson.getBytes());
//        payloadStream.write(signature);
//        // payloadStream.write(certification); // 인증서도 포함 시
//
//        byte[] encryptedPayload = CryptoUtil.encryptAES(payloadStream.toByteArray(), aesKey);
//
//        // 6. AES키를 서버의 공개키로 RSA 암호화
//        PublicKey serverPublicKey = keyStoreService.getServerPublicKey();
//        byte[] encryptedKey = CryptoUtil.encryptRSA(aesKey.getEncoded(), serverPublicKey);
//
//        // 7. 전자봉투 생성
//        ByteArrayOutputStream envelopeStream = new ByteArrayOutputStream();
//        envelopeStream.write(encryptedPayload);
//        envelopeStream.write(encryptedKey);
//        byte[] digitalEnvelope = envelopeStream.toByteArray();
//
//        // 8. 저장
//        FuturesContract contract = FuturesContract.builder()
//                .type(request.getType())
//                .position(request.getPosition())
//                .asset(request.getAsset())
//                .amount(request.getAmount())
//                .strikePrice(request.getStrikePrice())
//                .expiration(request.getExpiration())
//                .timestamp(LocalDateTime.now())
//                .member(member)
//                .matchCode(request.getMatchCode())
//                .digitalEnvelope(digitalEnvelope)
//                .matchedContract(null)
//                .build();
//
//        FuturesContract savedContract = futuresContractRepository.save(contract);
//
//        futuresContractRepository.flush(); // 추가
//
//        System.out.println("저장된 ID: " + savedContract.getId());
//    }