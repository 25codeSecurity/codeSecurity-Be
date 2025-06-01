/*
파일명 : FuturesContractService.java
파일설명 : 계약 조건 비교해서 계약 체결하고 DB 저장하는 서비스 코드
작성자 : 김소망
기간 : 2025-05-31
*/
package code_security.coin_futures.service.FuturesContractService;

import code_security.coin_futures.crypto.CryptoUtil;
import code_security.coin_futures.domain.FuturesContract;
import code_security.coin_futures.domain.Member;
import code_security.coin_futures.repository.FuturesContractRepository.FuturesContractRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FuturesContractCommandServiceImpl implements FuturesContractCommandService {

    }
}

