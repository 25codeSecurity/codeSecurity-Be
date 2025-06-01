/*
파일명 : BeanLogger.java
파일설명 : Spring이 실제로 어떤 빈을 등록했는지 로그로 확인하는 코드
작성자 : 김소망
기간 : 2025-05-31
*/

package code_security.coin_futures;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class BeanLogger implements CommandLineRunner {

    private final ApplicationContext context;

    public BeanLogger(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void run(String... args) {
        System.out.println("🟢 등록된 Bean 목록:");
        for (String name : context.getBeanDefinitionNames()) {
            if (name.toLowerCase().contains("contractpage")) {
                System.out.println("✅ " + name);
            }
        }
    }
}

