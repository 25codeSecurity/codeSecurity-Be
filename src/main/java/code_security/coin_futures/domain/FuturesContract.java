package code_security.coin_futures.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuturesContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    private String matchCode;

    private String position;

    private String asset; //거래 코인 정보

    private Integer amount;

    private Integer strikePrice;

    private LocalDate expiration;

    private LocalDateTime timestamp;

    private boolean matched;   // 계약이 체결되었는지
    private boolean settled;   // 정산이 완료되었는지
    private Double settlementAmount; // 손익 금액

    @Lob
    private byte[] digitalEnvelope; // 전자봉투

    //계약 당사자 n:1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // FK
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matched_contract_id")
    private FuturesContract matchedContract;
}
