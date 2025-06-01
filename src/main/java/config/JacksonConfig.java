/*
파일명 : JacksonConfig.java
파일설명 : jackson-datatype-jsr310 의존성 추가했으므로 ObjectMapper에 JavaTimeModule 등록하는 코드
작성자 : 김소망
기간 : 2025-06-01
*/
package config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public Module javaTimeModule() {
        return new JavaTimeModule();
    }
}