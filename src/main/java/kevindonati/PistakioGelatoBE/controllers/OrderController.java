package kevindonati.PistakioGelatoBE.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kevindonati.PistakioGelatoBE.entities.Order;
import kevindonati.PistakioGelatoBE.enums.OrderStatus;
import kevindonati.PistakioGelatoBE.payloads.CheckoutDTO;
import kevindonati.PistakioGelatoBE.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "Orders", description = "Order management")
@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public Page<Order> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "createdAt") String orderBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String customer,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Double minTotal,
            @RequestParam(required = false) Double maxTotal,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dateFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dateTo
    ) {
        return orderService.findAllWithFilters(
                page,
                size,
                orderBy,
                direction,
                id,
                userId,
                customer,
                status,
                minTotal,
                maxTotal,
                dateFrom,
                dateTo
        );
    }

    @GetMapping("/{id}/shipping-cost")
    @SecurityRequirement(name = "bearerAuth")
    public double calculateShippingCost(@PathVariable UUID id) {
        return orderService.calculateShippingCost(id);
    }

    @GetMapping("/cart")
    @SecurityRequirement(name = "bearerAuth")
    public Order findCart() {
        return orderService.findCart();
    }

    @GetMapping("/my")
    @SecurityRequirement(name = "bearerAuth")
    public Page<Order> findMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String orderBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return orderService.findMyOrders(
                page,
                size,
                orderBy,
                direction
        );
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public Order findById(@PathVariable UUID id) {
        return orderService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerAuth")
    public Order save() {
        return orderService.save();
    }

    @PutMapping("/{id}/checkout")
    @SecurityRequirement(name = "bearerAuth")
    public Order checkout(@PathVariable UUID id, @RequestBody @Validated CheckoutDTO payload) {
        return orderService.checkout(id, payload);
    }

    @PutMapping("/{id}/cancel")
    @SecurityRequirement(name = "bearerAuth")
    public Order cancelOrder(@PathVariable UUID id) {
        return orderService.cancelOrder(id);
    }

    @PutMapping("/{id}/prepare")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public Order startPreparation(@PathVariable UUID id) {
        return orderService.startPreparation(id);
    }
}
