package code_security.coin_futures.web.dto.FuturesContractDTO;

import lombok.Data;

import java.time.LocalDate;

public class FuturesContractResponseDTO {
    @Data
    public static class SubmitContractResultDTO{
        Long contractId;
    }
}
