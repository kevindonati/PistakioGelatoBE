package kevindonati.PistakioGelatoBE.controllers;

import kevindonati.PistakioGelatoBE.entities.Order;
import kevindonati.PistakioGelatoBE.payloads.CheckoutDTO;
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
                               @RequestParam(defaultValue = "createdAt") String orderBy,
                               @RequestParam(defaultValue = "desc") String direction) {
        return orderService.findAll(page, size, orderBy, direction);
    }

    @GetMapping("/cart")
    public Order findCart() {
        return orderService.findCart();
    }

    @GetMapping("/my")
    public Page<Order> findMyOrders(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(defaultValue = "desc") String direction) {
        return orderService.findMyOrders(page, size, direction);
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
    @PutMapping("/{id}/prepare")
    public Order startPreparation(@PathVariable UUID id) {
        return orderService.startPreparation(id);
    }
}
