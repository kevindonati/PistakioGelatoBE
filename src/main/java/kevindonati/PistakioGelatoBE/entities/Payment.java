package kevindonati.PistakioGelatoBE.entities;

import jakarta.persistence.*;
import kevindonati.PistakioGelatoBE.enums.PaymentStatus;
import kevindonati.PistakioGelatoBE.enums.ProviderType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProviderType provider;

    @Column(name = "id_transaction", nullable = false)
    private String idTransaction;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @OneToOne
    @JoinColumn(name = "id_order", nullable = false)
    private Order order;

    public Payment(ProviderType provider, String idTransaction, double amount, String currency, PaymentStatus status, Order order) {
        this.provider = provider;
        this.idTransaction = idTransaction;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.paymentDate = LocalDateTime.now();
        this.order = order;
    }
}
