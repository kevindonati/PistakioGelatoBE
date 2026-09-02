package kevindonati.PistakioGelatoBE.services;


import kevindonati.PistakioGelatoBE.entities.Order;
import kevindonati.PistakioGelatoBE.entities.OrderItem;
import kevindonati.PistakioGelatoBE.entities.Payment;
import kevindonati.PistakioGelatoBE.enums.OrderStatus;
import kevindonati.PistakioGelatoBE.enums.PaymentStatus;
import kevindonati.PistakioGelatoBE.enums.ProviderType;
import kevindonati.PistakioGelatoBE.exceptions.BadRequestException;
import kevindonati.PistakioGelatoBE.repositories.OrderItemRepository;
import kevindonati.PistakioGelatoBE.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;


import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class PaypalService {
    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderService orderService;
    private final EmailService emailService;
    private final RestClient restClient;
    private final String clientId;
    private final String clientSecret;
    private final String baseUrl;
    private final String successUrl;
    private final String cancelUrl;

    private final tools.jackson.databind.ObjectMapper objectMapper = new tools.jackson.databind.ObjectMapper();

    public PaypalService(PaymentRepository paymentRepository,
                         OrderItemRepository orderItemRepository,
                         OrderService orderService,
                         EmailService emailService,
                         @Value("${paypal.client-id}") String clientId,
                         @Value("${paypal.client-secret}") String clientSecret,
                         @Value("${paypal.base-url}") String baseUrl,
                         @Value("${paypal.success-url}") String successUrl,
                         @Value("${paypal.cancel-url}") String cancelUrl) {
        this.paymentRepository = paymentRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderService = orderService;
        this.emailService = emailService;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.baseUrl = baseUrl;
        this.successUrl = successUrl;
        this.cancelUrl = cancelUrl;
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    private String getAccessToken() {
        String response = restClient
                .post()
                .uri("/v1/oauth2/token")
                .headers(headers -> {
                    headers.setBasicAuth(clientId, clientSecret);
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                })
                .body("grant_type=client_credentials")
                .retrieve()
                .body(String.class);

        try {
            tools.jackson.databind.JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            throw new BadRequestException("Unable to authenticate with PayPal");
        }
    }

    public PaypalCreateResponse createOrder(Order order) {
        if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BadRequestException("The order is not waiting for payment");
        }

        if (order.getTotal() <= 0) {
            throw new BadRequestException("The order total must be greater than zero");
        }

        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

        if (orderItems.isEmpty()) {
            throw new BadRequestException("The order has no items");
        }

        Payment existingPayment = paymentRepository.findByOrder(order).orElse(null);


        System.out.println("=== PAYPAL PAYMENT DEBUG ===");

        if (existingPayment == null) {
            System.out.println("existingPayment = null");
        } else {
            System.out.println("provider = " + existingPayment.getProvider());
            System.out.println("status = " + existingPayment.getStatus());
            System.out.println("transaction = " + existingPayment.getIdTransaction());
        }

        System.out.println("============================");

        if (existingPayment != null && existingPayment.getStatus() != PaymentStatus.FAILED && existingPayment.getProvider() != ProviderType.PAYPAL) {
            throw new BadRequestException("This order already has a payment with another provider");
        }

        String accessToken = getAccessToken();

        Map<String, Object> amount = new HashMap<>();
        amount.put("currency_code", "EUR");
        amount.put(
                "value",
                String.format(Locale.US, "%.2f", order.getTotal())
        );

        Map<String, Object> purchaseUnit = new HashMap<>();
        purchaseUnit.put(
                "reference_id",
                order.getId().toString()
        );
        purchaseUnit.put("amount", amount);

        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put("intent", "CAPTURE");
        requestBody.put(
                "purchase_units",
                List.of(purchaseUnit)
        );

        Map<String, Object> applicationContext =
                new HashMap<>();

        applicationContext.put(
                "return_url",
                successUrl + "?orderId=" + order.getId()
        );

        applicationContext.put(
                "cancel_url",
                cancelUrl + "?orderId=" + order.getId()
        );

        applicationContext.put(
                "user_action",
                "PAY_NOW"
        );

        requestBody.put(
                "application_context",
                applicationContext
        );

        String response = restClient
                .post()
                .uri("/v2/checkout/orders")
                .headers(headers -> {
                    headers.setBearerAuth(accessToken);
                    headers.setContentType(
                            MediaType.APPLICATION_JSON
                    );
                })
                .body(requestBody)
                .retrieve()
                .body(String.class);
        System.out.println("PayPal Create Order response:");
        System.out.println(response);
        try {

            tools.jackson.databind.JsonNode jsonNode =
                    objectMapper.readTree(response);

            String paypalOrderId =
                    jsonNode.get("id").asText();

            String approvalUrl = null;

            for (JsonNode link : jsonNode.get("links")) {

                if ("approve".equals(
                        link.get("rel").asText()
                )) {
                    approvalUrl =
                            link.get("href").asText();
                    break;
                }
            }

            if (approvalUrl == null) {
                throw new BadRequestException(
                        "PayPal approval URL not found"
                );
            }

            Payment payment;

            if (existingPayment != null) {
                if (existingPayment.getStatus() != PaymentStatus.FAILED) {
                    throw new BadRequestException("This order already has a payment");
                }
                payment = existingPayment;
                payment.setProvider(ProviderType.PAYPAL);
                payment.setIdTransaction(paypalOrderId);
                payment.setAmount(order.getTotal());
                payment.setCurrency("EUR");
                payment.setStatus(PaymentStatus.PENDING);
                payment.setStripeEventId(null);
            } else {
                payment = new Payment(
                        ProviderType.PAYPAL,
                        paypalOrderId,
                        order.getTotal(),
                        "EUR",
                        PaymentStatus.PENDING,
                        order
                );
            }
            paymentRepository.save(payment);
            return new PaypalCreateResponse(paypalOrderId, approvalUrl);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Unable to create PayPal order");
        }
    }

    public Payment captureOrder(String paypalOrderId) {

        Payment payment = paymentRepository
                .findByIdTransaction(paypalOrderId)
                .orElseThrow(() -> new BadRequestException("PayPal payment not found"));

        if (payment.getProvider() != ProviderType.PAYPAL) {
            throw new BadRequestException("Payment is not a PayPal payment");
        }

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            return payment;
        }

        String accessToken = getAccessToken();

        String response = restClient
                .post()
                .uri(
                        "/v2/checkout/orders/"
                                + paypalOrderId
                                + "/capture"
                )
                .headers(headers -> {
                    headers.setBearerAuth(accessToken);
                    headers.setContentType(
                            MediaType.APPLICATION_JSON
                    );
                })
                .body("{}")
                .retrieve()
                .body(String.class);

        try {

            tools.jackson.databind.JsonNode jsonNode =
                    objectMapper.readTree(response);

            String status = jsonNode.get("status").asText();

            if (!"COMPLETED".equals(status)) {

                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                throw new BadRequestException("PayPal payment was not completed");
            }

            payment.setStatus(PaymentStatus.COMPLETED);

            Order order = orderService.confirmPayment(payment.getOrder().getId());

            orderService.decreaseStock(order);

            emailService.sendPaymentCompleteEmail(order);

            return paymentRepository.save(payment);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Unable to capture PayPal payment");
        }
    }

    public record PaypalCreateResponse(String orderId, String approvalUrl) {
    }
}