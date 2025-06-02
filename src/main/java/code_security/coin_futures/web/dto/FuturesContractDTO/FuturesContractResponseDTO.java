package code_security.coin_futures.web.dto.FuturesContractDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
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
        private Integer amount;
        private Integer strikePrice;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate expiration;
        private String user;
        private LocalDateTime timestamp;
        private Boolean matched;
        private Boolean settled;
        private Double settlementAmount;
    }
}

