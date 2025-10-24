package desafio.java.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "emails")
@Data
public class EmailEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String email;

    private boolean validated = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private ClientEntity client;
}

