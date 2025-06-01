package code_security.coin_futures.web.controller;

import code_security.coin_futures.domain.FuturesContract;
import code_security.coin_futures.service.FuturesContractService.FuturesContractCommandService;
import code_security.coin_futures.web.dto.FuturesContractDTO.FuturesContractRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class FuturesContractController {
    private final FuturesContractCommandService futuresContractCommandService;

    @PostMapping("/submit")
    public ResponseEntity<String> submitFuturesContract(@RequestBody FuturesContractRequestDTO.SubmitContractDTO request){
        try {
            futuresContractCommandService.submitContract(request, request.getMemberId());
            return ResponseEntity.ok("Contract submitted successfully.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
