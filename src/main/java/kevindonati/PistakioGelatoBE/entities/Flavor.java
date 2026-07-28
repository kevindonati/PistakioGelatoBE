package kevindonati.PistakioGelatoBE.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "flavors")
@Getter
@Setter
@NoArgsConstructor
public class Flavor {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false, unique = true, name = "referral_code")
    private String referralCode;

    private String image;

    @Column(nullable = false, name = "stock_portions")
    private int stockPortions;

    @Column(nullable = false)
    private boolean available;

    @Column(nullable = false)
    private boolean vegan;

    @Column(nullable = false, name = "lactose_free")
    private boolean lactoseFree;

    @Column(nullable = false, name = "gluten_free")
    private boolean glutenFree;

    @Column(nullable = false, name = "sugar_free")
    private boolean sugarFree;

    public Flavor(String name, String description, String referralCode, String image, int stockPortions, boolean available, boolean vegan, boolean lactoseFree, boolean glutenFree, boolean sugarFree) {
        this.name = name;
        this.description = description;
        this.referralCode = referralCode;
        this.image = image;
        this.stockPortions = stockPortions;
        this.available = available;
        this.vegan = vegan;
        this.lactoseFree = lactoseFree;
        this.glutenFree = glutenFree;
        this.sugarFree = sugarFree;
    }
}
