package code_security.coin_futures.web.dto.FuturesContractDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
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

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate expiration;

    }
}
