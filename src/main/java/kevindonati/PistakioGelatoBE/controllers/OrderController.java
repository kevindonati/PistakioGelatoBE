package kevindonati.PistakioGelatoBE.controllers;

import kevindonati.PistakioGelatoBE.entities.Order;
import kevindonati.PistakioGelatoBE.payloads.CheckoutDTO;
import kevindonati.PistakioGelatoBE.payloads.OrderCreateDTO;
import kevindonati.PistakioGelatoBE.payloads.OrderResponseDTO;
import kevindonati.PistakioGelatoBE.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public Page<Order> findAll(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(defaultValue = "name") String orderBy
    ) {
        return orderService.findAll(page, size, orderBy);
    }

    @GetMapping("/{id}")
    public Order findById(@PathVariable UUID id) {
        return orderService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order save() {
        return orderService.save();
    }

    @PutMapping("/{id}/checkout")
    public Order checkout(@PathVariable UUID id, @RequestBody @Validated CheckoutDTO payload) {
        return orderService.checkout(id, payload);
    }

    @PutMapping("/{id}/cancel")
    public Order cancelOrder(@PathVariable UUID id) {
        return orderService.cancelOrder(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/confirm-payment")
    public Order confirmPayment(@PathVariable UUID id) {
        return orderService.confirmPayment(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/prepare")
    public Order startPreparation(@PathVariable UUID id) {
        return orderService.startPreparation(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/ship")
    public Order shipOrder(@PathVariable UUID id) {
        return orderService.shipOrder(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/deliver")
    public Order deliverOrder(@PathVariable UUID id) {
        return orderService.deliverOrder(id);
    }
}
