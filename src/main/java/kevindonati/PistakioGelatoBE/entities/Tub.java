package kevindonati.PistakioGelatoBE.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "tubs")
@Getter
@Setter
@NoArgsConstructor
public class Tub {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private int weight;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false, name = "max_flavors")
    private int maxFlavors;

    private String image;

    @Column(nullable = false)
    private boolean available;

    public Tub(String name, String description, int weight, double price, int maxFlavors, String image, boolean available) {
        this.name = name;
        this.description = description;
        this.weight = weight;
        this.price = price;
        this.maxFlavors = maxFlavors;
        this.image = image;
        this.available = available;
    }
}
