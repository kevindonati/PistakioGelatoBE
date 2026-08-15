package kevindonati.PistakioGelatoBE.controllers;

import kevindonati.PistakioGelatoBE.entities.Order;
import kevindonati.PistakioGelatoBE.entities.Payment;
import kevindonati.PistakioGelatoBE.payloads.PaymentStripeResponseDTO;
import kevindonati.PistakioGelatoBE.services.OrderService;
import kevindonati.PistakioGelatoBE.services.PaymentService;
import kevindonati.PistakioGelatoBE.services.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private OrderService orderService;

    @GetMapping
    public Page<Payment> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "paymentDate") String orderBy
    ) {
        return paymentService.findAll(page, size, orderBy);
    }

    @GetMapping("/{id}")
    public Payment findById(@PathVariable UUID id) {
        return paymentService.findById(id);
    }

    @PostMapping("/stripe/{orderId}")
    public PaymentStripeResponseDTO createStripeCheckout(@PathVariable UUID orderId) {
        Order order = orderService.findById(orderId);
        String checkoutUrl = stripeService.createCheckoutSession(order);

        return new PaymentStripeResponseDTO(checkoutUrl);
    }
}
