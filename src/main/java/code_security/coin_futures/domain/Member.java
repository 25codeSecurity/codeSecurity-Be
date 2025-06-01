package code_security.coin_futures.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    private String email;

    private String password;

    private String phone;

    private String address;

    private Integer age;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<FuturesContract> futuresContracts = new ArrayList<>();


}
