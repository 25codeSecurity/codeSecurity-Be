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

    private String position;

    private String asset;

    private Double amount;

    private Double strikePrice;

    private LocalDate expiration;

    private String user;

    private LocalDateTime timestamp;

    @Lob
    private byte[] digitalEnvelope;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // FK
    private Member member;
}
