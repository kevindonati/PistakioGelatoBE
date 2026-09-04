package kevindonati.PistakioGelatoBE.entities;

import jakarta.persistence.*;
import kevindonati.PistakioGelatoBE.enums.ShipmentStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
public class Shipment {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(nullable = false)
    private String carrier;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "shipping_date", nullable = false)
    private LocalDate shippingDate;

    @Column(name = "estimated_delivery")
    private LocalDate estimatedDelivery;
    
    @Column(name = "delivered_at")
    private LocalDate deliveredAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status;

    @OneToOne
    @JoinColumn(name = "id_order", nullable = false)
    private Order order;

    public Shipment(String carrier, String trackingNumber, ShipmentStatus status, Order order) {
        this.carrier = carrier;
        this.trackingNumber = trackingNumber;
        this.shippingDate = LocalDate.now();
        this.estimatedDelivery = shippingDate.plusDays(3);
        this.deliveredAt = null;
        this.status = status;
        this.order = order;
    }
}
