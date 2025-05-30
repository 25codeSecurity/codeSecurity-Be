package code_security.coin_futures.service.FuturesContractService;

import code_security.coin_futures.domain.FuturesContract;
import code_security.coin_futures.repository.FuturesContractRepository.FuturesContractRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FuturesContractCommandServiceImpl implements FuturesContractCommandService {

    private FuturesContractRepository futuresContractRepository;

}
