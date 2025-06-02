/*
파일명 : TestDataInitializer.java
파일설명 : Step 3 테스트용 기본 데이터 DB 넣는 코드
작성자 : 김소망
기간 : 2025-05-31
*/
package code_security.coin_futures;

import code_security.coin_futures.domain.FuturesContract;
import code_security.coin_futures.domain.Member;
import code_security.coin_futures.repository.FuturesContractRepository;
import code_security.coin_futures.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TestDataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final FuturesContractRepository contractRepository;

    @Override
    public void run(String... args) {
        Member userA = Member.builder()
                .name("Alice")
                .email("alice@example.com")
                .password("pass")
                .age(30)
                .address("Seoul")
                .phone("010-1111-1111")
                .build();

        Member userB = Member.builder()
                .name("Bob")
                .email("bob@example.com")
                .password("pass")
                .age(32)
                .address("Busan")
                .phone("010-2222-2222")
                .build();

        memberRepository.saveAll(List.of(userA, userB));

        // Step 3 테스트용 계약 (포지션 반대, 조건 동일)
        contractRepository.save(
                FuturesContract.builder()
                        .type("futures_contract")
                        .position("long")
                        .asset("BTC")
                        .amount(1)
                        .strikePrice(60000)
                        .expiration(LocalDate.of(2025, 6, 30))
                        .timestamp(LocalDateTime.now())
                        .member(userA)
                        .matchCode("111")
                        .matched(false)
                        .settled(false)
                        .build()
        );

        contractRepository.save(
                FuturesContract.builder()
                        .type("futures_contract")
                        .position("short")
                        .asset("BTC")
                        .amount(1)
                        .strikePrice(60000)
                        .expiration(LocalDate.of(2025, 6, 30))
                        .timestamp(LocalDateTime.now())
                        .member(userB)
                        .matchCode("111")
                        .matched(false)
                        .settled(false)
                        .build()
        );

        System.out.println("✅ 테스트 데이터 삽입 완료!");
    }
}
