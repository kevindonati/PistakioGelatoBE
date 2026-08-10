package kevindonati.PistakioGelatoBE.services;

import kevindonati.PistakioGelatoBE.entities.Address;
import kevindonati.PistakioGelatoBE.entities.Order;
import kevindonati.PistakioGelatoBE.entities.User;
import kevindonati.PistakioGelatoBE.enums.OrderStatus;
import kevindonati.PistakioGelatoBE.enums.UserRole;
import kevindonati.PistakioGelatoBE.exceptions.BadRequestException;
import kevindonati.PistakioGelatoBE.exceptions.NotFoundException;
import kevindonati.PistakioGelatoBE.exceptions.UnauthorizedException;
import kevindonati.PistakioGelatoBE.payloads.CheckoutDTO;
import kevindonati.PistakioGelatoBE.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AddressService addressService;

    private User getAuthenticatedUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return (User) authentication.getPrincipal();
    }

    public Order findById(UUID id) {
        Order foundedOrder = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Order with id " + id + " not found"));
        User authenticatedUser = getAuthenticatedUser();

        if (authenticatedUser.getRole() != UserRole.ADMIN && !foundedOrder.getUser().getId().equals(authenticatedUser.getId())) {
            throw new UnauthorizedException("You cannot access another user's order");
        }
        return foundedOrder;
    }

    public Order save() {
        User authenticatedUser = getAuthenticatedUser();
        Order newOrder = new Order(
                OrderStatus.CART,
                0.0,
                0.0,
                null,
                authenticatedUser,
                null);
        return orderRepository.save(newOrder);
    }

    public Order checkout(UUID id, CheckoutDTO payload) {
        Order foundedOrder = this.findById(id);
        Address foundedAddress = addressService.findById(payload.address());

        if (foundedOrder.getOrderStatus() != OrderStatus.CART) {
            throw new BadRequestException("Only carts can be checked out");
        }

        foundedOrder.setAddress(foundedAddress);
        foundedOrder.setNotes(payload.notes());

        // TODO: calcolare il costo della spedizione
        foundedOrder.setShippingCost(6.00);

        // TODO: ricalcolare il totale dagli OrderItem
        foundedOrder.setTotal(foundedOrder.getTotal() + foundedOrder.getShippingCost());

        foundedOrder.setOrderStatus(OrderStatus.PENDING_PAYMENT);
        foundedOrder.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(foundedOrder);
    }

    public Order confirmPayment(UUID id) {
        Order foundedOrder = this.findById(id);
        if (foundedOrder.getOrderStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BadRequestException("Order is not waiting for payment");
        }

        foundedOrder.setOrderStatus(OrderStatus.PAID);
        foundedOrder.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(foundedOrder);
    }

    public Order startPreparation(UUID id) {
        Order foundedOrder = this.findById(id);

        if (foundedOrder.getOrderStatus() != OrderStatus.PAID) {
            throw new BadRequestException("Order has not been paid");
        }

        foundedOrder.setOrderStatus(OrderStatus.PREPARING);
        foundedOrder.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(foundedOrder);
    }

    public Order shipOrder(UUID id) {
        Order foundedOrder = this.findById(id);

        if (foundedOrder.getOrderStatus() != OrderStatus.PREPARING) {
            throw new BadRequestException("Order is not ready to be shipped");
        }

        foundedOrder.setOrderStatus(OrderStatus.SHIPPED);
        foundedOrder.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(foundedOrder);
    }

    public Order deliverOrder(UUID id) {
        Order foundedOrder = this.findById(id);

        if (foundedOrder.getOrderStatus() != OrderStatus.SHIPPED) {
            throw new BadRequestException("Order has not been shipped");
        }

        foundedOrder.setOrderStatus(OrderStatus.DELIVERED);
        foundedOrder.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(foundedOrder);
    }

    public Order cancelOrder(UUID id) {
        Order foundedOrder = this.findById(id);
        if (foundedOrder.getOrderStatus() != OrderStatus.CART && foundedOrder.getOrderStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BadRequestException("This order cannot be cancelled at this stage");
        }

        foundedOrder.setOrderStatus(OrderStatus.CANCELLED);
        foundedOrder.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(foundedOrder);
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public Page<Order> findAll(int page, int size, String orderBy) {
        if (size > 50) size = 50;
        if (size < 1) size = 10;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));

        User authenticatedUser = getAuthenticatedUser();
        if (authenticatedUser.getRole() == UserRole.ADMIN) {
            return orderRepository.findAll(pageable);
        }

        return orderRepository.findByUserId(authenticatedUser.getId(), pageable);
    }


}
