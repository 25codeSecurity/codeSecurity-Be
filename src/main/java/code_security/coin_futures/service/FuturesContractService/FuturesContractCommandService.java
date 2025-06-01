package code_security.coin_futures.service.FuturesContractService;

import code_security.coin_futures.domain.FuturesContract;
import code_security.coin_futures.web.dto.FuturesContractDTO.FuturesContractRequestDTO;

public interface FuturesContractCommandService {

    //계약서 제출
    void submitContract(FuturesContractRequestDTO.SubmitContractDTO request, Long userId)throws Exception;
}
