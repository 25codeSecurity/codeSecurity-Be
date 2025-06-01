package code_security.coin_futures.web.controller;

import code_security.coin_futures.domain.FuturesContract;
import code_security.coin_futures.web.dto.FuturesContractDTO.FuturesContractResponseDTO;
import code_security.coin_futures.repository.FuturesContractRepository.FuturesContractRepository;
import code_security.coin_futures.service.FuturesContractService.FuturesContractCommandService;
import code_security.coin_futures.service.SettlementService;
import code_security.coin_futures.web.dto.FuturesContractDTO.FuturesContractRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class FuturesContractController {
    private final FuturesContractCommandService futuresContractCommandService;
    private final SettlementService settlementService;
    private final FuturesContractRepository futuresContractRepository;

    @PostMapping("/submit")
    public ResponseEntity<String> submitFuturesContract(@RequestBody FuturesContractRequestDTO.SubmitContractDTO request){
        try {
            futuresContractCommandService.submitContract(request, request.getMemberId());
            return ResponseEntity.ok("Contract submitted successfully.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //계약 체결 API
    @PostMapping("/match")
    public ResponseEntity<String> matchContracts(@RequestBody MatchRequest request) {
        try {
            futuresContractCommandService.matchContracts(request.contractId1(), request.contractId2());
            return ResponseEntity.ok("✅ 계약 체결 성공");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ 계약 체결 실패: " + e.getMessage());
        }
    }
    public record MatchRequest(Long contractId1, Long contractId2) {}

    //정산 API
    @PostMapping("/settle")
    public ResponseEntity<String> settleContracts(@RequestBody SettleRequest request) {
        try {
            settlementService.settleContracts(
                    request.contractId1(),
                    request.contractId2(),
                    request.currentPrice()
            );
            return ResponseEntity.ok("✅ 계약 정산 완료");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ 정산 실패: " + e.getMessage());
        }
    }
    public record SettleRequest(Long contractId1, Long contractId2, Double currentPrice) {}

    //계약 상세 조회 API
    @GetMapping("/{id}")
    public ResponseEntity<FuturesContractResponseDTO.ContractDetailDTO> getContract(@PathVariable Long id) {
        return futuresContractRepository.findById(id)
                .map(contract -> ResponseEntity.ok(
                        FuturesContractResponseDTO.ContractDetailDTO.builder()
                                .id(contract.getId())
                                .position(contract.getPosition())
                                .asset(contract.getAsset())
                                .amount(contract.getAmount())
                                .strikePrice(contract.getStrikePrice())
                                .expiration(contract.getExpiration())
                                .user(contract.getMember().getName())
                                .timestamp(contract.getTimestamp())
                                .matched(contract.isMatched())
                                .settled(contract.isSettled())
                                .settlementAmount(contract.getSettlementAmount())
                                .build()
                ))
                .orElse(ResponseEntity.notFound().build());
    }
}


