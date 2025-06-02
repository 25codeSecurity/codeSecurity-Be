package code_security.coin_futures;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan(basePackages = "code_security.coin_futures")
public class CoinFuturesApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoinFuturesApplication.class, args);
	}

}
