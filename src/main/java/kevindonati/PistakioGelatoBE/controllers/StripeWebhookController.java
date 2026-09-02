package kevindonati.PistakioGelatoBE.controllers;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import kevindonati.PistakioGelatoBE.services.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class StripeWebhookController {

    private final String webhookSecret;
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    public StripeWebhookController(
            @Value("${stripe.webhook-secret}") String webhookSecret,
            PaymentService paymentService,
            ObjectMapper objectMapper
    ) {
        this.webhookSecret = webhookSecret;
        this.paymentService = paymentService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signature) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }
        try {

            if ("checkout.session.completed".equals(event.getType())) {
                String rawJson = event.getDataObjectDeserializer().getRawJson();
                JsonNode jsonNode = objectMapper.readTree(rawJson);
                String sessionId = jsonNode.get("id").asText();
                Session session = Session.retrieve(sessionId);
                String orderId = session.getClientReferenceId();

                System.out.println("Stripe session: " + sessionId);
                System.out.println("Order collegato: " + orderId);

                paymentService.completeStripePayment(sessionId, event.getId());
            } else if ("payment_intent.payment_failed".equals(event.getType())) {
                String rawJson = event.getDataObjectDeserializer().getRawJson();
                JsonNode jsonNode = objectMapper.readTree(rawJson);
                String paymentIntentId = jsonNode.get("id").asText();
                String orderId = jsonNode
                        .get("metadata")
                        .get("order_id")
                        .asText();

                System.out.println("Stripe payment failed: " + paymentIntentId);
                System.out.println("Order collegato: " + orderId);

                paymentService.failStripePaymentByOrderId(UUID.fromString(orderId));
            }
            return ResponseEntity.ok("Webhook received");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error processing webhook");
        }
    }
}