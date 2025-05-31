package code_security.coin_futures.repository.FuturesContractRepository;

import code_security.coin_futures.domain.FuturesContract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuturesContractRepository extends JpaRepository<FuturesContract, Long>, FuturesContractRepositoryCustom {
}
