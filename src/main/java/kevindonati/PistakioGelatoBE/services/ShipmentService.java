package kevindonati.PistakioGelatoBE.services;

import kevindonati.PistakioGelatoBE.entities.Order;
import kevindonati.PistakioGelatoBE.entities.Shipment;
import kevindonati.PistakioGelatoBE.entities.User;
import kevindonati.PistakioGelatoBE.enums.OrderStatus;
import kevindonati.PistakioGelatoBE.enums.ShipmentStatus;
import kevindonati.PistakioGelatoBE.enums.UserRole;
import kevindonati.PistakioGelatoBE.exceptions.BadRequestException;
import kevindonati.PistakioGelatoBE.exceptions.NotFoundException;
import kevindonati.PistakioGelatoBE.payloads.ShipmentDTO;
import kevindonati.PistakioGelatoBE.repositories.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class ShipmentService {
    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private OrderService orderService;

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }


    public Shipment save(ShipmentDTO payload) {
        Order foundedOrder = orderService.findById(payload.order());

        if (foundedOrder.getOrderStatus() != OrderStatus.PAID && foundedOrder.getOrderStatus() != OrderStatus.PREPARING) {
            throw new BadRequestException("Order is not ready to be shipped");
        }

        if (shipmentRepository.existsByOrder(foundedOrder)) {
            throw new BadRequestException("Shipment already exists for this order");
        }

        Shipment shipment = new Shipment(payload.carrier(), payload.trackingNumber(), ShipmentStatus.PENDING, foundedOrder);
        return shipmentRepository.save(shipment);
    }

    public Page<Shipment> findAll(int page, int size, String orderBy) {
        if (size > 50) size = 50;
        if (size < 1) size = 10;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));

        User authenticatedUser = getAuthenticatedUser();
        if (authenticatedUser.getRole() == UserRole.ADMIN) {
            return shipmentRepository.findAll(pageable);
        }
        return shipmentRepository.findByOrderUserId(authenticatedUser.getId(), pageable);
    }

    public Shipment findById(UUID id) {
        Shipment foundedShipment = shipmentRepository.findById(id).orElseThrow(() -> new NotFoundException("Shipment with id " + id + " not found"));
        orderService.findById(foundedShipment.getOrder().getId());
        return foundedShipment;
    }

    public Shipment updateStatus(UUID id, ShipmentStatus status) {
        Shipment foundedShipment = this.findById(id);
        foundedShipment.setStatus(status);

        if (status == ShipmentStatus.SHIPPED) {
            orderService.shipOrder(foundedShipment.getOrder().getId());
        }

        if (status == ShipmentStatus.DELIVERED) {
            foundedShipment.setDeliveredAt(LocalDate.now());
            orderService.deliverOrder(foundedShipment.getOrder().getId());
        }

        return shipmentRepository.save(foundedShipment);
    }

    public void findByIdAndDelete(UUID id) {
        Shipment foundedShipment = this.findById(id);
        shipmentRepository.delete(foundedShipment);
    }
}
