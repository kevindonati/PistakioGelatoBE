package kevindonati.PistakioGelatoBE.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StripeService {
    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;
    private final String successUrl;
    private final String cancelUrl;

    public StripeService(OrderItemRepository orderItemRepository, PaymentRepository paymentRepository, @Value("${stripe.secret-key}") String secretKey, @Value("${stripe.success-url}") String successUrl, @Value("${stripe.cancel-url}") String cancelUrl) {
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.successUrl = successUrl;
        this.cancelUrl = cancelUrl;
        Stripe.apiKey = secretKey;
    }

    public String createCheckoutSession(Order order) {
        if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BadRequestException("The order is not waiting for payment");
        }

        if (order.getTotal() <= 0) {
            throw new BadRequestException("The order total must be greater than zero");
        }

        if (paymentRepository.existsByOrder(order)) {
            throw new BadRequestException("This order already has a payment");
        }

        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

        if (orderItems.isEmpty()) {
            throw new BadRequestException("The order has no items");
        }

        try {
            SessionCreateParams.Builder sessionBuilder =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl(successUrl)
                            .setCancelUrl(cancelUrl)
                            .setClientReferenceId(order.getId().toString())
                            .putMetadata(
                                    "order_id",
                                    order.getId().toString()
                            );

            for (OrderItem item : orderItems) {
                String productName = item.getFlavor().getName() + " - " + item.getTub().getName();

                SessionCreateParams.LineItem lineItem =
                        SessionCreateParams.LineItem.builder()
                                .setQuantity((long) item.getQuantity())
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData
                                                .builder()
                                                .setCurrency("eur")
                                                .setUnitAmount(
                                                        Math.round(item.getUnitPrice() * 100)
                                                )
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData
                                                                .builder()
                                                                .setName(productName)
                                                                .build()
                                                )
                                                .build()
                                )
                                .build();
                sessionBuilder.addLineItem(lineItem);
            }

            if (order.getShippingCost() > 0) {
                SessionCreateParams.LineItem shippingItem =
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData
                                                .builder()
                                                .setCurrency("eur")
                                                .setUnitAmount(
                                                        Math.round(order.getShippingCost() * 100)
                                                )
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData
                                                                .builder()
                                                                .setName("Spedizione")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build();
                sessionBuilder.addLineItem(shippingItem);
            }

            Session session = Session.create(sessionBuilder.build());

            Payment payment = new Payment(
                    ProviderType.STRIPE,
                    session.getId(),
                    order.getTotal(),
                    "EUR",
                    PaymentStatus.PENDING,
                    order
            );
            paymentRepository.save(payment);
            return session.getUrl();
        } catch (StripeException e) {
            throw new BadRequestException("Unable to create Stripe checkout session");
        }
    }
}
