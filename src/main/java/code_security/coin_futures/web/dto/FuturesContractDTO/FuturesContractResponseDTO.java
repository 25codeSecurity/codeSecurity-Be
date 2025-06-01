package code_security.coin_futures.web.dto.FuturesContractDTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FuturesContractResponseDTO {

    @Data
    public static class SubmitContractResultDTO {
        Long contractId;
    }

    @Data
    @Builder
    public static class ContractDetailDTO {
        private Long id;
        private String position;
        private String asset;
        private Double amount;
        private Double strikePrice;
        private LocalDate expiration;
        private String user;
        private LocalDateTime timestamp;
        private Boolean matched;
        private Boolean settled;
        private Double settlementAmount;
    }
}

