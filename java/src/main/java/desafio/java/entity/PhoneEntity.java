package desafio.java.entity;

import desafio.java.enums.PhoneType;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "phones")
@Data
public class PhoneEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PhoneType type;

    @Column(nullable = false, length = 11)
    private String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private ClientEntity client;

}

