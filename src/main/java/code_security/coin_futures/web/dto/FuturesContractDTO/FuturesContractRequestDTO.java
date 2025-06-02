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
        private String matchCode;
        private String asset;
        private Integer amount;
        private Integer strikePrice;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate expiration;

        // (선택) 디버깅을 위한 toString() 오버라이드
        @Override
        public String toString() {
            return String.format(
                    "SubmitContractDTO[memberId=%d, type=%s, asset=%s, amount=%d, expiration=%s]",
                    memberId, type, asset, amount, expiration
            );
        }
    }
}
