package kevindonati.PistakioGelatoBE.controllers;

import jakarta.validation.Valid;
import kevindonati.PistakioGelatoBE.entities.OrderItem;
import kevindonati.PistakioGelatoBE.payloads.OrderItemDTO;
import kevindonati.PistakioGelatoBE.payloads.OrderItemResponseDTO;
import kevindonati.PistakioGelatoBE.services.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/order-items")
public class OrderItemsController {
    @Autowired
    private OrderItemService orderItemService;

    @GetMapping
    public Page<OrderItem> findAll(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(defaultValue = "id") String orderBy
    ) {
        return orderItemService.findAll(page, size, orderBy);
    }

    @GetMapping("/{id}")
    public OrderItem findById(@PathVariable UUID id) {
        return orderItemService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderItemResponseDTO save(@RequestBody @Validated OrderItemDTO payload) {
        OrderItem savedOrderItem = orderItemService.save(payload);
        return new OrderItemResponseDTO(savedOrderItem.getId());
    }

    @PutMapping("/{id}")
    public OrderItem findByIdAndUpdate(@PathVariable UUID id, @RequestBody @Valid OrderItemDTO payload) {
        return orderItemService.findByIdAndUpdate(id, payload);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void findByIdAndDelete(@PathVariable UUID id) {
        orderItemService.findByIdAndDelete(id);
    }
}
