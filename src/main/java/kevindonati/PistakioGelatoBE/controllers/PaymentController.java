package kevindonati.PistakioGelatoBE.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kevindonati.PistakioGelatoBE.entities.Order;
import kevindonati.PistakioGelatoBE.entities.Payment;
import kevindonati.PistakioGelatoBE.payloads.PaymentStripeResponseDTO;
import kevindonati.PistakioGelatoBE.services.OrderService;
import kevindonati.PistakioGelatoBE.services.PaymentService;
import kevindonati.PistakioGelatoBE.services.PaypalService;
import kevindonati.PistakioGelatoBE.services.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Payments", description = "Payment management")
@RestController
@RequestMapping("/payments")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaypalService paypalService;

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

    @GetMapping("/order/{orderId}")
    public Payment findByOrderId(@PathVariable UUID orderId) {
        return paymentService.findByOrderId(orderId);
    }

    @PostMapping("/stripe/{orderId}")
    public PaymentStripeResponseDTO createStripeCheckout(@PathVariable UUID orderId) {
        Order order = orderService.findById(orderId);
        String checkoutUrl = stripeService.createCheckoutSession(order);

        return new PaymentStripeResponseDTO(checkoutUrl);
    }

    @PostMapping("/paypal/{orderId}")
    public PaypalService.PaypalCreateResponse createPaypalOrder(@PathVariable UUID orderId) {
        Order order = orderService.findById(orderId);
        return paypalService.createOrder(order);
    }

    @PostMapping("/paypal/{paypalOrderId}/capture")
    public Payment capturePaypalOrder(@PathVariable String paypalOrderId) {
        return paypalService.captureOrder(paypalOrderId);
    }
}
