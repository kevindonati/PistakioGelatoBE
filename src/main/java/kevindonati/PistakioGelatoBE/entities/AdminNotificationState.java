package kevindonati.PistakioGelatoBE.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "admin_notification_states", uniqueConstraints = {@UniqueConstraint(columnNames = "id_admin")})
@Getter
@Setter
@NoArgsConstructor
public class AdminNotificationState {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "id_admin", nullable = false, unique = true)
    private User admin;

    @Column(name = "last_orders_seen_at", nullable = false)
    private LocalDateTime lastOrdersSeenAt;

    @Column(name = "last_customers_seen_at", nullable = false)
    private LocalDateTime lastCustomersSeenAt;

    public AdminNotificationState(User admin) {
        this.admin = admin;
        LocalDateTime now = LocalDateTime.now();
        this.lastOrdersSeenAt = now;
        this.lastCustomersSeenAt = now;
    }
}