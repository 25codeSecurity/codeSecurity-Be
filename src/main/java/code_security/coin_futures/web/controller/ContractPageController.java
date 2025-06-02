/*
파일명 : SettlementService.java
파일설명 : 타임리프용 @Controller
작성자 : 김소망
기간 : 2025-06-01
*/

package code_security.coin_futures.web.controller;

import code_security.coin_futures.domain.FuturesContract;
import code_security.coin_futures.repository.FuturesContractRepository;
import code_security.coin_futures.service.FuturesContractService.FuturesContractCommandService;
import code_security.coin_futures.service.SettlementService;
import code_security.coin_futures.web.dto.FuturesContractDTO.FuturesContractRequestDTO;
import code_security.coin_futures.web.dto.FuturesContractDTO.FuturesContractResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contracts")
@RequiredArgsConstructor
public class ContractPageController {

    private final SettlementService settlementService;
    private final FuturesContractCommandService commandService;
    private final FuturesContractRepository contractRepository;
    private final FuturesContractCommandService futuresContractCommandService;

    @GetMapping("/contract-form")
    public String contractFormPage() {
        return "contractForm"; // templates/contractForm.html
    }

    @PostMapping("/submit")
    public String handleSubmit(@ModelAttribute FuturesContractRequestDTO.SubmitContractDTO request, Model model) {
        try {
            futuresContractCommandService.submitContract(request, request.getMemberId());
            model.addAttribute("message", "Contract submitted successfully.");
        } catch (Exception e) {
            model.addAttribute("message", "Error: " + e.getMessage());
        }
        return "contractform";
    }
    // Step 3: 계약 체결 폼
    @GetMapping("/match-form")
    public String showMatchPage() {
        return "match";
    }

    @PostMapping("/match")
    public String matchContracts(
            @RequestParam Long contractId1,
            @RequestParam Long contractId2,
            Model model
    ) {
        try {
            commandService.matchContracts(contractId1, contractId2);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "match";
    }

    // Step 4: 정산 폼
    @GetMapping("/settle-form")
    public String showSettlePage() {
        return "settle";
    }

    @PostMapping("/settle")
    public String settleContracts(
            @RequestParam Long contractId1,
            @RequestParam Long contractId2,
            @RequestParam Double currentPrice,
            Model model
    ) {
        try {
            settlementService.settleContracts(contractId1, contractId2, currentPrice);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "settle";
    }

    // Step 5: 계약 상세 보기 (숫자만 허용)
    @GetMapping("/detail/{id:[0-9]+}")
    public String viewContract(@PathVariable Long id, Model model) {
        FuturesContract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("계약 없음"));

        FuturesContractResponseDTO.ContractDetailDTO dto = FuturesContractResponseDTO.ContractDetailDTO.builder()
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
                .build();

        model.addAttribute("contract", dto);
        return "detail";
    }
}
