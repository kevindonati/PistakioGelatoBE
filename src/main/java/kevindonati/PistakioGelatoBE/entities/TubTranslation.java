package kevindonati.PistakioGelatoBE.entities;

import jakarta.persistence.*;
import kevindonati.PistakioGelatoBE.enums.Language;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "tub_translations")
@Getter
@Setter
@NoArgsConstructor
public class TubTranslation {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language language;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "id_tub", nullable = false)
    private Tub tub;

    public TubTranslation(Language language, String name, String description, Tub tub) {
        this.language = language;
        this.name = name;
        this.description = description;
        this.tub = tub;
    }
}
