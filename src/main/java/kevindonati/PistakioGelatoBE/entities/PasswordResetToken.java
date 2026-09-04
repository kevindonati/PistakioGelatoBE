package kevindonati.PistakioGelatoBE.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne
    @JoinColumn(name = "id_user", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    public PasswordResetToken(
            String token,
            User user,
            LocalDateTime expiresAt
    ) {
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
    }
}