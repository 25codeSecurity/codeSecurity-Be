package code_security.coin_futures.web.dto.FuturesContractDTO;

import lombok.Data;

@Data
public class EnvelopePayloadDTO {
    private FuturesContractRequestDTO.SubmitContractDTO contract;
    private String signature;
    private String cert;
}
