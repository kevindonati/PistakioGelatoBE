package kevindonati.PistakioGelatoBE.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, name = "unit_price")
    private double unitPrice;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_order")
    private Order order;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_flavor")
    private Flavor flavor;

    @ManyToOne
    @JoinColumn(name = "id_tub", nullable = false)
    private Tub tub;

    public OrderItem(int quantity, double unitPrice, Order order, Flavor flavor, Tub tub) {
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.order = order;
        this.flavor = flavor;
        this.tub = tub;
    }
}
