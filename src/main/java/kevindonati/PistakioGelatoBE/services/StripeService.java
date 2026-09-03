package kevindonati.PistakioGelatoBE.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import kevindonati.PistakioGelatoBE.entities.*;
import kevindonati.PistakioGelatoBE.enums.Language;
import kevindonati.PistakioGelatoBE.enums.OrderStatus;
import kevindonati.PistakioGelatoBE.enums.PaymentStatus;
import kevindonati.PistakioGelatoBE.enums.ProviderType;
import kevindonati.PistakioGelatoBE.exceptions.BadRequestException;
import kevindonati.PistakioGelatoBE.repositories.FlavorTranslationRepository;
import kevindonati.PistakioGelatoBE.repositories.OrderItemRepository;
import kevindonati.PistakioGelatoBE.repositories.PaymentRepository;
import kevindonati.PistakioGelatoBE.repositories.TubTranslationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StripeService {
    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;
    private final String successUrl;
    private final String cancelUrl;
    private final FlavorTranslationRepository flavorTranslationRepository;
    private final TubTranslationRepository tubTranslationRepository;

    public StripeService(OrderItemRepository orderItemRepository, PaymentRepository paymentRepository, @Value("${stripe.secret-key}") String secretKey, @Value("${stripe.success-url}") String successUrl, @Value("${stripe.cancel-url}") String cancelUrl, FlavorTranslationRepository flavorTranslationRepository, TubTranslationRepository tubTranslationRepository) {
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.successUrl = successUrl;
        this.cancelUrl = cancelUrl;
        Stripe.apiKey = secretKey;
        this.flavorTranslationRepository = flavorTranslationRepository;
        this.tubTranslationRepository = tubTranslationRepository;
    }

    public String createCheckoutSession(Order order) {
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

        Payment existingPayment = paymentRepository.findByOrder(order)
                .orElse(null);

        if (existingPayment != null &&
                existingPayment.getStatus() != PaymentStatus.PENDING &&
                existingPayment.getStatus() != PaymentStatus.FAILED) {
            throw new BadRequestException("This order has already been paid");
        }
        try {
            SessionCreateParams.Builder sessionBuilder =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl(successUrl + "?orderId=" + order.getId())
                            .setCancelUrl(cancelUrl + "?orderId=" + order.getId() + "&provider=STRIPE")
                            .setClientReferenceId(order.getId().toString())
                            .putMetadata(
                                    "order_id",
                                    order.getId().toString()
                            )
                            .setPaymentIntentData(
                                    SessionCreateParams.PaymentIntentData.builder()
                                            .putMetadata(
                                                    "order_id",
                                                    order.getId().toString()
                                            )
                                            .build()
                            );
            Language language = order.getUser().getLanguage();
            for (OrderItem item : orderItems) {

                FlavorTranslation flavorTranslation = flavorTranslationRepository.findByFlavorAndLanguage(item.getFlavor(), language)
                        .orElseThrow(() -> new BadRequestException("Flavor translation not found for language " + language));

                TubTranslation tubTranslation = tubTranslationRepository.findByTubAndLanguage(item.getTub(), language)
                        .orElseThrow(() -> new BadRequestException("Tub translation not found for language " + language));

                String productName = flavorTranslation.getName() + " - " + tubTranslation.getName();

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

            Payment payment;

            if (existingPayment != null) {
                payment = existingPayment;
                payment.setIdTransaction(session.getId());
                payment.setAmount(order.getTotal());
                payment.setCurrency("EUR");
                payment.setStatus(PaymentStatus.PENDING);
                payment.setPaymentDate(java.time.LocalDateTime.now());
                payment.setProvider(ProviderType.STRIPE);
                payment.setStripeEventId(null);
            } else {
                payment = new Payment(
                        ProviderType.STRIPE,
                        session.getId(),
                        order.getTotal(),
                        "EUR",
                        PaymentStatus.PENDING,
                        order
                );
            }

            paymentRepository.save(payment);
            return session.getUrl();
        } catch (StripeException e) {
            throw new BadRequestException("Unable to create Stripe checkout session");
        }
    }
}
