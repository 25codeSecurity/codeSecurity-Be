package code_security.coin_futures.repository;

import code_security.coin_futures.domain.FuturesContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuturesContractRepository extends JpaRepository<FuturesContract, Long>, FuturesContractRepositoryCustom {
}
