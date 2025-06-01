package code_security.coin_futures.web.dto.FuturesContractDTO;

import lombok.Data;

import java.time.LocalDate;

public class FuturesContractRequestDTO {

    @Data
    public static class SubmitContractDTO{
        private Long memberId;
        private String type;
        private String position;
        private String asset;
        private Double amount;
        private Double strikePrice;
        private LocalDate expiration;
    }
}
