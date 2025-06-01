/*
파일명 : SettlementService.java
파일설명 : 계약 정산 처리 서비스 코드
작성자 : 김소망
기간 : 2025-05-31
*/
package code_security.coin_futures.service;

import code_security.coin_futures.domain.FuturesContract;
import code_security.coin_futures.repository.FuturesContractRepository.FuturesContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final FuturesContractRepository contractRepository;

    public void settleContracts(Long contractId1, Long contractId2, double currentPrice) {
        FuturesContract c1 = contractRepository.findById(contractId1)
                .orElseThrow(() -> new RuntimeException("계약 1 없음"));
        FuturesContract c2 = contractRepository.findById(contractId2)
                .orElseThrow(() -> new RuntimeException("계약 2 없음"));

        if (c1.isSettled() || c2.isSettled()) {
            throw new IllegalStateException("이미 정산된 계약입니다.");
        }

        if (!c1.getExpiration().isBefore(LocalDate.now()) && !c1.getExpiration().isEqual(LocalDate.now())) {
            throw new IllegalStateException("아직 만기일이 도달하지 않았습니다.");
        }

        // 포지션 기준 정산 방향 확인
        FuturesContract longContract = c1.getPosition().equals("long") ? c1 : c2;
        FuturesContract shortContract = c1.getPosition().equals("short") ? c1 : c2;

        double strike = longContract.getStrikePrice();
        double amount = longContract.getAmount();

        double pnl = (currentPrice - strike) * amount;

        // 결과 저장
        longContract.setSettlementAmount(pnl);         // 이익
        shortContract.setSettlementAmount(-pnl);       // 손해

        longContract.setSettled(true);
        shortContract.setSettled(true);

        contractRepository.save(longContract);
        contractRepository.save(shortContract);

        System.out.println("✅ 정산 완료: Long PnL = " + pnl + ", Short PnL = " + (-pnl));
    }
}

