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
    private int weight;

    @Column(nullable = false)
    private double price;

    private String image;

    @Column(nullable = false)
    private boolean available;

    public Tub(int weight, double price, String image, boolean available) {
        this.weight = weight;
        this.price = price;
        this.image = image;
        this.available = available;
    }
}
