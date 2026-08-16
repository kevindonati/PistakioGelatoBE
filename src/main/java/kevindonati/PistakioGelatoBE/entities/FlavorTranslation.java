package kevindonati.PistakioGelatoBE.entities;

import jakarta.persistence.*;
import kevindonati.PistakioGelatoBE.enums.Language;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "flavor_translations")
@Getter
@Setter
@NoArgsConstructor
public class FlavorTranslation {

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
    @JoinColumn(name = "id_flavor", nullable = false)
    private Flavor flavor;

    public FlavorTranslation(Language language, String name, String description, Flavor flavor) {
        this.language = language;
        this.name = name;
        this.description = description;
        this.flavor = flavor;
    }
}
